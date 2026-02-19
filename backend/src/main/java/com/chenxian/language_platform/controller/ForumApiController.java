package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CommentDTO;
import com.chenxian.language_platform.dto.PostDto;
import com.chenxian.language_platform.entity.Comment;
import com.chenxian.language_platform.entity.Language;
import com.chenxian.language_platform.entity.Post;
import com.chenxian.language_platform.repository.CommentRepository;
import com.chenxian.language_platform.repository.LanguageRepository;
import com.chenxian.language_platform.repository.LikeRepository;
import com.chenxian.language_platform.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 論壇 REST API（貼文、留言、按讚）
 */
@RestController
@RequestMapping("/api/forum")
public class ForumApiController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LikeRepository likeRepository;

    // ==================== 貼文 ====================

    /** 取得所有貼文 */
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts(
            @RequestParam(required = false) Long languageId) {
        List<Post> posts;
        if (languageId != null) {
            posts = postRepository.findByLanguageIdAndIsDeletedFalse(languageId);
        } else {
            posts = postRepository.findByIsDeletedFalseOrderByCreateTimeDesc();
        }
        return ResponseEntity.ok(posts);
    }

    /** 取得單一貼文 */
    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        return postRepository.findById(id)
                .map(post -> ResponseEntity.ok((Object) post))
                .orElse(ResponseEntity.notFound().build());
    }

    /** 建立貼文 */
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto,
                                         HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setUserId(userId);
        post.setLanguageId(postDto.getLanguageId());
        post.setCreateTime(LocalDateTime.now());
        post.setIsDeleted(false);

        Post saved = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** 更新貼文 */
    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id,
                                         @RequestBody PostDto postDto,
                                         HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return postRepository.findById(id).map(post -> {
            if (!post.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body((Object) Map.of("message", "無權限修改此貼文"));
            }
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            return ResponseEntity.ok((Object) postRepository.save(post));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** 刪除貼文（軟刪除） */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id,
                                         HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer levelId = (Integer) request.getAttribute("levelId");

        return postRepository.findById(id).map(post -> {
            if (!post.getUserId().equals(userId) && levelId != 2) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body((Object) Map.of("message", "無權限刪除此貼文"));
            }
            post.setIsDeleted(true);
            postRepository.save(post);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // ==================== 按讚 ====================

    /** 按讚貼文 */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Integer postId,
                                       HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "已按讚"));
        }
        likeRepository.addLike(userId, postId);
        return ResponseEntity.ok(Map.of("message", "按讚成功"));
    }

    /** 取消按讚 */
    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Integer postId,
                                         HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        likeRepository.removeLike(userId, postId);
        return ResponseEntity.noContent().build();
    }

    /** 取得貼文按讚數 */
    @GetMapping("/posts/{postId}/likes/count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Integer postId) {
        return ResponseEntity.ok(likeRepository.countByPostId(postId));
    }

    /** 確認用戶是否已按讚 */
    @GetMapping("/posts/{postId}/likes/check")
    public ResponseEntity<Boolean> checkLiked(@PathVariable Integer postId,
                                               HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return ResponseEntity.ok(likeRepository.existsByUserIdAndPostId(userId, postId));
    }

    // ==================== 留言 ====================

    /** 取得留言列表 */
    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getComments(
            @RequestParam(required = false) Integer postId) {
        List<Comment> comments;
        if (postId != null) {
            comments = commentRepository.findByPostIdOrderByCreateTimeAsc(postId);
        } else {
            comments = commentRepository.findAll();
        }
        return ResponseEntity.ok(comments);
    }

    /** 取得單一留言 */
    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Integer id) {
        return commentRepository.findById(id)
                .map(c -> ResponseEntity.ok((Object) c))
                .orElse(ResponseEntity.notFound().build());
    }

    /** 新增留言 */
    @PostMapping("/comments")
    public ResponseEntity<?> createComment(@RequestBody CommentDTO dto,
                                            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setUserId(userId);
        comment.setPostId(dto.getPostId());
        comment.setCreateTime(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** 更新留言 */
    @PutMapping("/comments/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Integer id,
                                            @RequestBody CommentDTO dto,
                                            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return commentRepository.findById(id).map(comment -> {
            if (!comment.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body((Object) Map.of("message", "無權限修改此留言"));
            }
            comment.setContent(dto.getContent());
            return ResponseEntity.ok((Object) commentRepository.save(comment));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** 刪除留言 */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer id,
                                            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer levelId = (Integer) request.getAttribute("levelId");

        return commentRepository.findById(id).map(comment -> {
            if (!comment.getUserId().equals(userId) && levelId != 2) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body((Object) Map.of("message", "無權限"));
            }
            commentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // ==================== 語言分類 ====================

    /** 取得所有語言分類 */
    @GetMapping("/languages")
    public ResponseEntity<List<Language>> getLanguages() {
        return ResponseEntity.ok(languageRepository.findAll());
    }
}
