package com.chenxian.language_platform.repository;

import com.chenxian.language_platform.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
    // 可以根據需要添加自訂的查詢方法
}