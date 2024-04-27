package com.myworld.gradution_project_backend.VO;

import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @ClassName GptModel
 * @Descripton 不同GPT模型接口需要传入的参数汇总到当前类
 * @Author wkz
 * @Date 2024/4/11 16:20
 * @Version 1.0
 */
@Data
@Builder
public class GptModel {
    private String tencent_prompt;
    private Long tencent_logoAdd;
    private String tencent_style;

    private String openai_model;
    private String openai_prompt;
    private Integer openai_n;
    private String openai_size;
    private List<ChatMessage> openai_chatMessageList;
    private Double openai_temperature;
    private Integer openai_maxToken;
    private Double openai_topP;
}
