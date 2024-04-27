package com.myworld.gradution_project_backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

import static com.myworld.gradution_project_backend.utils.Constants.SIGNING_KEY;

public class JWTUtils {

    /**
     * 生成token  header.payload.singature
     */
    public static String getToken(Map<String, String> map) {

        Calendar instance = Calendar.getInstance();
        // 默认1天过期
        instance.add(Calendar.DATE, 1);
//        instance.add(Calendar.SECOND, 20);
        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();

        // payload
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        String token = builder.withExpiresAt(instance.getTime())  //指定令牌过期时间
                .sign(Algorithm.HMAC256(SIGNING_KEY));  // sign
        return token;
    }

    /**
     * 验证token  合法性
     */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SIGNING_KEY)).build().verify(token);
    }

    /**
     * 获取token信息方法
     */
    public static DecodedJWT getTokenInfo(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGNING_KEY)).build().verify(token);
        return verify;
    }
}


