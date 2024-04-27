package com.myworld.gradution_project_backend.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * @ClassName Result
 * @Descripton TODO
 * @Author wkz
 * @Date 2023/11/16 9:57
 * @Version 1.0
 */
public class Result extends HashMap<String, Object> {
    public static Result success(){
        Result map = new Result();
        map.put("code", 200);
        map.put("msg", "success");
        return map;
    }
    public static Result success(Integer code,String msg){
        Result map = new Result();
        map.put("code", code);
        map.put("msg", msg);
        return map;
    }
    public static Result error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部异常");
    }
    public static Result error(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }
    public static Result error(int code, String msg) {
        Result r = new Result();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }
    // 添加或覆盖Result的数据
    public Result put(Object value){
        super.put("data", value);
        return this;
    }
    public Result put(String key,Object value){
        super.put(key,value);
        return this;
    }
}
