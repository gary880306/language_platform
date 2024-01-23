package com.chenxian.language_platform.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Data
public class Coupon {
    private Integer couponId;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;
    private boolean isActive;
    private Integer quantity;
    private Boolean isDeleted;
    // 枚舉類型，用於表示折扣類型
    public enum DiscountType {
        FIXED,PERCENTAGE
    }

    // 返回優惠券狀態的修改後方法
    public String getStatus() {
        Calendar current = Calendar.getInstance();
        // 取得當前日期並將時間設為 0
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);
        Date today = current.getTime();

        // 創建一個臨時變量來存儲調整後的結束日期
        Date adjustedEndDate = null;
        if (endDate != null) {
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            adjustedEndDate = endCal.getTime();
        }

        // 使用臨時變量進行日期比對
        if (adjustedEndDate != null && adjustedEndDate.before(today)) {
            return "expired";
        } else if (startDate != null && startDate.after(today)) {
            return "notStarted";
        } else {
            return "active";
        }
    }

}
