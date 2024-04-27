package com.myworld.gradution_project_backend.DTO;

import com.myworld.gradution_project_backend.Enum.DialogRole;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DialogRespDto
 * @Descripton 包含对话的简略信息，用处有返回某次对话的部分信息
 * @Author wkz
 * @Date 2024/4/4 16:20
 * @Version 1.0
 */
@Data
public class DialogRespDto {
    /**
     * 聊天内容
     */
    private String content;

    private List<String> fileNameList = new ArrayList<>();

    private List<String> fileUrlList = new ArrayList<>();
}
