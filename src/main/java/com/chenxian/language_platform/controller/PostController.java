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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Integer, Integer> postLikes = new HashMap<>(); // 用於存儲帖子點讚數的映射

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
    public Post updatePost(@PathVariable Integer id, @RequestBody Post updatedPost) {
        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost != null) {
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setContent(updatedPost.getContent());
            // Update other properties as needed
            return postRepository.save(existingPost);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Integer id) {
        postRepository.deleteById(id);
    }

    //--------------------------------------------------------------------------------------------
    // 點讚區
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Integer postId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶未登錄");
        }

        // 檢查用戶是否已經點讚，如果沒有，則進行點讚
        if (!hasLiked(user.getUserId(), postId)) {
            // 創建一個新的按讚信息對象
            Like likes = new Like();
            likes.setUser(user); // 設置用戶

            // 根據 postId 獲取相應的帖子對象
            Post post = postRepository.findById(postId).orElse(null);
            // 設置帖子到 Like 對象中
            likes.setPostId(post.getId()); // 設置帖子ID
            likes.setLikedAt(LocalDateTime.now()); // 設置按讚時間

            // 保存按讚信息到數據庫
            likeRepository.save(likes);

            // 更新帖子的按讚數（如果需要的話）
            postLikes.put(postId, postLikes.getOrDefault(postId, 0) + 1);
        }

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Integer postId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶未登錄");
        }

        // 檢查用戶是否已經點讚，如果已經點讚，則取消點讚
        if (hasLiked(user.getUserId(), postId)) {
            postLikes.put(postId, postLikes.getOrDefault(postId, 0) - 1);
            // 在這裡添加從數據庫中刪除點讚信息的邏輯，如果需要的話
        }

        return ResponseEntity.ok().build();
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
        return false;
    }
}
