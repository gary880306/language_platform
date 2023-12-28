package com.chenxian.language_platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {
    @GetMapping("/admin/reportInfo")
    public String showAllReport(){
        return "admin/reportInfo";
    }
}
