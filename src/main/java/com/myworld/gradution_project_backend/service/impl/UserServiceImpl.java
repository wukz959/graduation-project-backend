package com.myworld.gradution_project_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myworld.gradution_project_backend.bean.User;
import com.myworld.gradution_project_backend.service.UserService;
import com.myworld.gradution_project_backend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author wkz
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-03-18 15:03:41
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public void saveUser(User user) {
        Long isExists = query().eq("username", user.getUsername()).count();
        if (isExists == 0L) {
            save(user);
        }
    }
}




