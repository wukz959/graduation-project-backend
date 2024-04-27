package com.myworld.gradution_project_backend.Enum;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @ClassName DialogRole
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/4/3 15:35
 * @Version 1.0
 */
public enum DialogRole {
    SYSTEM("system"),USER("user"),ASSISTANT("assistant"),IMAGE("image");
    private String name;
    DialogRole(String name){
        this.name = name;
    }

    /**
     * 获取name值，注意：这不是mybatis-plus封装的用来映射MySQL属性的方法，这里只起到返回name值作用
     * @return
     */
    public String getName(){
        return this.name();
    }
}
