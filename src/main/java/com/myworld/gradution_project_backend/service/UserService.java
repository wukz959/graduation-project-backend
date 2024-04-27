package com.myworld.gradution_project_backend.service;

import com.myworld.gradution_project_backend.bean.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author wkz
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-03-18 15:03:41
*/
public interface UserService extends IService<User> {

    void saveUser(User user);
}
