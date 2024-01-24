package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.PostDto;
import com.chenxian.language_platform.entity.Language;
import com.chenxian.language_platform.entity.Like;
import com.chenxian.language_platform.entity.Post;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.repository.LanguageRepository;
import com.chenxian.language_platform.repository.LikeRepository;
import com.chenxian.language_platform.repository.PostRepository;
import com.chenxian.language_platform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/enjoyLearning/Posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeRepository likeRepository;
    private Map<Integer, Integer> postLikes = new HashMap<>(); // 用於存儲討論點讚數的映射

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Integer id) {
        return postRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Post createPost(@RequestBody PostDto postDto) {
        User user = userRepository.findById(postDto.getUserId()).orElse(null);
        Language language = languageRepository.findById(postDto.getLanguageId()).orElse(null);

        if (user != null && language != null) {
            Post post = new Post();
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            post.setUser(user); // 設置 User 實體
            post.setLanguage(language); // 設置 Language 實體
            return postRepository.save(post);
        }
        // 處理用戶或語言不存在的情況
        return null;
    }


    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Integer id, @RequestBody PostDto postDto) {
        Post existingPost = postRepository.findById(id).orElse(null);
        User user = userRepository.findById(postDto.getUserId()).orElse(null);
        Language language = languageRepository.findById(postDto.getLanguageId()).orElse(null);
        if (existingPost != null) {
            existingPost.setTitle(postDto.getTitle());
            existingPost.setContent(postDto.getContent());
            existingPost.setUser(user);
            existingPost.setLanguage(language);
            return postRepository.save(existingPost);
        }
        return null;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setDeleted(true);
                    postRepository.save(post);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }


    //--------------------------------------------------------------------------------------------
    // 點讚區
    @Transactional
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Integer postId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶未登錄");
        }

        // 根據 postId 獲取相應的討論對象
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到指定的帖子");
        }

        Like likes = new Like();
        likes.setUser(user); // 設置用戶
        likes.setPost(post); // 設置討論
        likes.setLikedAt(LocalDateTime.now()); // 設置按讚時間
        likeRepository.save(likes); // 保存按讚信息到數據庫
        // 更新討論的按讚數
        Integer like = likeRepository.countByPost(post);

        return ResponseEntity.ok(like);
    }

    @Transactional
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Integer postId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶未登錄");
        }

        Integer like = 0;
        // 檢查用戶是否已經點讚
        if (hasLiked(user.getUserId(), postId)) {
            // 從數據庫中刪除點讚信息
            likeRepository.deleteByUserAndPost(user, postRepository.findById(postId).orElse(null));
            Post post = postRepository.getReferenceById(postId);
            // 更新討論的按讚數
            like = likeRepository.countByPost(post);
        }

        return ResponseEntity.ok(like);
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Integer postId) {
        int count = postLikes.getOrDefault(postId, 0);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{postId}/likes/check")
    public ResponseEntity<Boolean> checkUserLiked(@PathVariable Integer postId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        boolean isLiked = hasLiked(user.getUserId(), postId);
        return ResponseEntity.ok(isLiked);
    }

    // 輔助方法，檢查用戶是否已經點讚
    private boolean hasLiked(Integer userId, Integer postId) {
        // 在這裡添加從數據庫或其他存儲中檢查用戶點讚狀態的邏輯
        // 返回 true 表示用戶已點讚，返回 false 表示用戶未點讚
        // 這裡只是一個示例，實際情況需要根據你的數據存儲方式來實現
        return likeRepository.existsByUser_UserIdAndPost_Id(userId, postId);
    }
}
