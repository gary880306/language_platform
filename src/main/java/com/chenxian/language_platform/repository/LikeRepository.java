package com.chenxian.language_platform.repository;

import com.chenxian.language_platform.entity.Like;
import com.chenxian.language_platform.entity.Post;
import com.chenxian.language_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    void deleteByUserAndPost(User user, Post post);
    boolean existsByUser_UserIdAndPost_Id(Integer userId, Integer postId);
}

