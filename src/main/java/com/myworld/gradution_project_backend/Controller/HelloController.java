package com.myworld.gradution_project_backend.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HelloController
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/3/17 15:20
 * @Version 1.0
 */
@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello(@RequestParam(required = false) String p1,@RequestParam(required = false) Boolean p2, String p3){
        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
        System.out.println("p3: " + p3);
        return "hello";
    }
}
