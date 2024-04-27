package com.myworld.gradution_project_backend.service;

import com.myworld.gradution_project_backend.DTO.DialogRespDto;
import com.myworld.gradution_project_backend.VO.FullDialog;
import com.myworld.gradution_project_backend.bean.Dialog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wkz
* @description 针对表【dialog(对话记录表)】的数据库操作Service
* @createDate 2024-03-27 15:27:39
*/
public interface DialogService extends IService<Dialog> {

    int getDialogCountByUserId(String userId);

    Dialog saveDialog(String content, Integer sessionSeq, String userId, Boolean isImage);

    List<FullDialog> getDialogHistoryByUserIdAndSessionSeq(String id,Integer sessionSeq);

    DialogRespDto getTextToTextResp(String userId, Integer sessionSeq, String content);
}
