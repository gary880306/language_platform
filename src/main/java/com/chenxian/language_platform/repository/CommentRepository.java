package com.chenxian.language_platform.repository;

import com.chenxian.language_platform.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // 可添加自定義查詢方法
}