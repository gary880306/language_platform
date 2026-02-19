package com.chenxian.language_platform.dto;

import lombok.Data;

import java.util.List;
@Data
public class SendCouponsRequest {
    private List<Integer> userIds;
    private List<Integer> couponIds;
}
