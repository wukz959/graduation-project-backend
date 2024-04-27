package com.myworld.gradution_project_backend.VO;

import com.myworld.gradution_project_backend.Enum.DialogRole;
import com.myworld.gradution_project_backend.bean.File;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FullDialog
 * @Descripton 封装一次完整对话，包括Dialog与File
 * @Author wkz
 * @Date 2024/3/29 22:58
 * @Version 1.0
 */
@Data
public class FullDialog {
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
     * 三个值：system, user, assistant。为每个dialog代表的角色,含义见ChatMessageRole类, IMAGE为包含图片的对话
     */
    private DialogRole role;

    private List<File> fileList;
    private String isImageMode;
    private List<String> fileNameList = new ArrayList<>();
    private List<String> fileUrlList = new ArrayList<>();

}
