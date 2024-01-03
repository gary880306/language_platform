package com.chenxian.language_platform.entity;

import com.chenxian.language_platform.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id") // 關聯到語言的ID
    private Language language;

    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
}
