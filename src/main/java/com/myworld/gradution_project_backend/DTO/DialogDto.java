package com.myworld.gradution_project_backend.DTO;

import com.myworld.gradution_project_backend.Enum.DialogRole;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DialogDto
 * @Descripton 返回对话的详细信息，包含图片信息，用处有查询某个会话下的全部聊天记录
 * @Author wkz
 * @Date 2024/4/4 14:44
 * @Version 1.0
 */
@Data
public class DialogDto {
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

    private DialogRole role;

    private List<String> fileNameList = new ArrayList<>();

    private List<String> fileUrlList = new ArrayList<>();
}
