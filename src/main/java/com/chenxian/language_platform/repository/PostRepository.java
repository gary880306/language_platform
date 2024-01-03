package com.chenxian.language_platform.repository;

import com.chenxian.language_platform.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    // 可添加自定義查詢方法
}
