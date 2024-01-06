package com.chenxian.language_platform.entity;

import com.chenxian.language_platform.model.User;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
import java.time.ZoneId;
import org.ocpsoft.prettytime.PrettyTime;
import java.util.Date;
import java.util.Locale;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(updatable = false)

    private LocalDateTime createTime = LocalDateTime.now();
    public String getFormatTimeAgo() {
        PrettyTime p = new PrettyTime(Locale.TRADITIONAL_CHINESE);
        String prettyTimeString = p.format(Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant()));

        // 檢查是否顯示為 "片刻之前"，如果是則替換為 "剛剛發布"
        if (prettyTimeString.equals("片刻之前")) {
            return "剛剛發布";
        }

        // 移除數字和時間單位之間的空格
        prettyTimeString = prettyTimeString.replace(" 分鐘 ", "分鐘");
        prettyTimeString = prettyTimeString.replace(" 小時 ", "小時");
        prettyTimeString = prettyTimeString.replace(" 天 ", "天");
        prettyTimeString = prettyTimeString.replace(" 月 ", "月");
        // 您可以根據需要繼續添加其他時間單位的替換邏輯

        return prettyTimeString;
    }

}
