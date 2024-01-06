package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CommentDTO;
import com.chenxian.language_platform.entity.Comment;
import com.chenxian.language_platform.entity.Post;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.repository.CommentRepository;
import com.chenxian.language_platform.repository.PostRepository;
import com.chenxian.language_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enjoyLearning/comments")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable Integer id) {
        return commentRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Comment createComment(@RequestBody CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());

        // 假设 CommentDTO 包含 userId 和 postId
        User user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        Post post = postRepository.findById(commentDTO.getPostId()).orElse(null);

        comment.setUser(user);
        comment.setPost(post);

        return commentRepository.save(comment);
    }


    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable Integer id, @RequestBody Comment updatedComment) {
        Comment existingComment = commentRepository.findById(id).orElse(null);
        if (existingComment != null) {
            existingComment.setContent(updatedComment.getContent());
            // Update other properties as needed
            return commentRepository.save(existingComment);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Integer id) {
        commentRepository.deleteById(id);
    }
}
