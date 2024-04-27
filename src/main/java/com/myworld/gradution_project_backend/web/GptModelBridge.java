package com.myworld.gradution_project_backend.web;

import com.myworld.gradution_project_backend.DTO.DialogRespDto;
import com.myworld.gradution_project_backend.VO.GptModel;
import com.myworld.gradution_project_backend.exception.GptModelException;
import com.myworld.gradution_project_backend.web.service.AllGptModel;

/**
 * @ClassName GptModelBridge
 * @Descripton 桥接模式适配不同模型
 * @Author wkz
 * @Date 2024/4/11 16:57
 * @Version 1.0
 */
public class GptModelBridge {
    private final AllGptModel allGptModel;
    public GptModelBridge(AllGptModel allGptModel)
    {
        this.allGptModel = allGptModel;
    }
    public String[] generateTextToImageResp(GptModel gptModel) throws GptModelException {
        return allGptModel.getTextToImageResp(gptModel);
    }
    public String generateTextToTextResp(GptModel gptModel){
        return allGptModel.getTextAnsByText(gptModel);
    }
}
