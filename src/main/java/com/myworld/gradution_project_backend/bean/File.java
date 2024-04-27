package com.myworld.gradution_project_backend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.*;

/**
 * 文件表-目前保存图片，后续可添加字段表示其他类型文件
 * @TableName file
 */
@TableName(value ="file")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class File implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件保存路径
     */
    private String fileUrl;

    /**
     * 文件初始名称
     */
    private String fileOriginName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件所在聊天记录的id
     */
    private Integer dialogId;

    /**
     * 
     */
    private String createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}