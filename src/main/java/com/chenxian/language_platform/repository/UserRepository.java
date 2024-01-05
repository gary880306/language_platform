package com.chenxian.language_platform.repository;

import com.chenxian.language_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
