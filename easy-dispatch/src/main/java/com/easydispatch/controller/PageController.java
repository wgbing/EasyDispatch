package com.easydispatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由：将根路径映射到前端页面（静态 HTML 由 static 目录提供）
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/dispatch")
    public String dispatch() {
        return "forward:/dispatch.html";
    }

    @GetMapping("/templates")
    public String templates() {
        return "forward:/templates.html";
    }
}
