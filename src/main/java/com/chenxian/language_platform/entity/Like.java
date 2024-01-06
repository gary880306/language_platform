package com.chenxian.language_platform.entity;

import com.chenxian.language_platform.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 關聯到帖子

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "liked_at")
    private LocalDateTime likedAt;

    public void setPostId(Integer id) {
        this.id = id;
    }

}
