package com.myworld.gradution_project_backend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.myworld.gradution_project_backend.Enum.DialogRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话记录表
 * @TableName dialog
 */
@TableName(value ="dialog")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dialog implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 第几次会话
     */
    private Integer sessionSeq;

    /**
     * 聊天记录顺序
     */
    private Integer dialogSeq;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 
     */
    private String createTime;

    /**
     * 四个值：SYSTEM,USER,ASSISTANT,IMAGE.前三个值为每个dialog代表的角色,含义见ChatMessageRole类; IMAGE为包含图片的对话
     */
    private DialogRole role;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}