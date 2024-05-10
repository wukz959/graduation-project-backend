package com.myworld.gradution_project_backend.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.myworld.gradution_project_backend.bean.User;
import com.myworld.gradution_project_backend.service.UserService;
import com.myworld.gradution_project_backend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * @ClassName LoginController
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/3/18 21:36
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public String login(@RequestBody User user){
        // 没验证密码等，一坨答辩代码
        HashMap<String,String> map = new HashMap();
        map.put("username",user.getUsername());
        user.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date()));
        userService.saveUser(user);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        QueryWrapper<User> queryWrapper = wrapper.select("id")
                .eq("username", user.getUsername());
        String id = userService.getOne(queryWrapper).getId();
        map.put("userId",id);
        String token = JWTUtils.getToken(map);
        return token;
    }
}
