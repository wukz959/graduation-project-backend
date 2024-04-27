package com.myworld.gradution_project_backend.utils;

import java.text.SimpleDateFormat;

/**
 * @ClassName TimeUtils
 * @Author wkz
 * @Date 2023/12/5 20:37
 * @Version 1.0
 */
public class TimeUtils {
    public static String getTimeNow(){
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(System.currentTimeMillis());
    }
}
