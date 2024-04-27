package com.myworld.gradution_project_backend.utils;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @ClassName LoginInterceptors
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/3/18 15:09
 * @Version 1.0
 */

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Result result = Result.error(401, "");
        // 获取请求头中令牌
        String token = request.getHeader("token");
        try {
            // 验证令牌
            JWTUtils.verify(token);
            return true;  // 放行请求
        } catch (SignatureVerificationException e) {
            result.put("msg","无效签名！");
        }catch (TokenExpiredException e){
            result.put("msg","token过期");
        }catch (AlgorithmMismatchException e){
            result.put("msg","算法不一致");
        }catch (Exception e){
            result.put("msg","token无效！");
        }
        // 将result以json的形式响应到前台  result --> json  (jackson)
        String json = new ObjectMapper().writeValueAsString(result);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}
