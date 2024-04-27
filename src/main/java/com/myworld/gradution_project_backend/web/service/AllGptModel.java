package com.myworld.gradution_project_backend.web.service;

import com.myworld.gradution_project_backend.DTO.DialogRespDto;
import com.myworld.gradution_project_backend.VO.GptModel;
import com.myworld.gradution_project_backend.exception.GptModelException;

/**
 *
 */
public interface AllGptModel {
    String[] getTextToImageResp(GptModel gptModel) throws GptModelException;

    default String getTextAnsByText(GptModel gptModel) {
        String ans = "Default implementation.This method is not implemented in the current class.";
        return ans;
    }

    ;
}
