package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.PostDto;
import com.chenxian.language_platform.entity.Language;
import com.chenxian.language_platform.entity.Post;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.repository.LanguageRepository;
import com.chenxian.language_platform.repository.PostRepository;
import com.chenxian.language_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enjoyLearning/Posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private UserRepository userRepository;

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
}
