package com.chenxian.language_platform.entity;

import com.chenxian.language_platform.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    private transient Integer languageId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id") // 關聯到語言的ID
    private Language language;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @ToString.Exclude  // 排除likes屬性
    @JsonIgnore
    private List<Like> likes;  // 新增一對多關係，用於存儲按讚記錄

    @Transient
    private boolean liked;
    private boolean isDeleted = false;

    public String getTimeAgo() {
        return formatTimeAgo(this.createTime);
    }

    public static String formatTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 1) return "剛剛發布";
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (days > 0) return days + "天前";
        if (hours > 0) return hours + "小時前";
        return minutes + "分鐘前";
    }

}
