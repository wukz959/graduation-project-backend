package com.myworld.gradution_project_backend.exception;

/**
 * @ClassName GptModelException
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/4/11 16:37
 * @Version 1.0
 */
public class GptModelException extends RuntimeException{
    public GptModelException(String message){
        super(message);
    }
}
