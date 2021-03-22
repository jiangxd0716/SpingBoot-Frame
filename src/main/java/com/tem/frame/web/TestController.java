package com.tem.frame.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 测试
     */
    @GetMapping
    public void test() {
        int i = 1;
        i = i / 0;
    }

}
