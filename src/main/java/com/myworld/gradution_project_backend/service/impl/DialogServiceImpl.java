package com.myworld.gradution_project_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myworld.gradution_project_backend.DTO.DialogRespDto;
import com.myworld.gradution_project_backend.Enum.DialogRole;
import com.myworld.gradution_project_backend.VO.FullDialog;
import com.myworld.gradution_project_backend.VO.GptModel;
import com.myworld.gradution_project_backend.bean.Dialog;
import com.myworld.gradution_project_backend.service.DialogService;
import com.myworld.gradution_project_backend.mapper.DialogMapper;
import com.myworld.gradution_project_backend.utils.JWTUtils;
import com.myworld.gradution_project_backend.utils.TimeUtils;
import com.myworld.gradution_project_backend.web.GptModelBridge;
import com.myworld.gradution_project_backend.web.service.impl.OpenaiGptModel;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wkz
 * @description 针对表【dialog(对话记录表)】的数据库操作Service实现
 * @createDate 2024-03-27 15:27:39
 */
@Service
public class DialogServiceImpl extends ServiceImpl<DialogMapper, Dialog>
        implements DialogService {

    @Autowired
    private OpenaiGptModel openaiGptModel;

    @Override
    public int getDialogCountByUserId(String userId) {
        return baseMapper.getDialogCountByUserId(userId);
    }

    @Override
    public Dialog saveDialog(String content, Integer sessionSeq, String userId, Boolean isImageReq) {
        String createTime = TimeUtils.getTimeNow();
        int dialogSeq = getDialogCountByUserId(userId);
        DialogRole nowRole = DialogRole.IMAGE;
        if (!isImageReq){
            QueryWrapper<Dialog> wrapper = new QueryWrapper<>();
            QueryWrapper<Dialog> queryWrapper = wrapper.eq("user_id", userId)
                    .eq("session_seq", sessionSeq)
                    .not(item -> item.eq("role","image"))
                    .orderByDesc("dialog_seq").last("limit 1");
            Dialog lastDialog = baseMapper.selectOne(queryWrapper);
            nowRole = (lastDialog==null || lastDialog.getRole() == DialogRole.ASSISTANT )? DialogRole.USER : DialogRole.ASSISTANT;
        }

        Dialog dialog = Dialog.builder().content(content).sessionSeq(sessionSeq)
                .role(nowRole)
                .createTime(createTime).dialogSeq(dialogSeq).userId(userId).build();
        save(dialog);
        return dialog;
    }

    @Override
    public List<FullDialog> getDialogHistoryByUserIdAndSessionSeq(String id, Integer sessionSeq) {
        List<FullDialog> dialogHistory = baseMapper.getDialogHistoryByUserIdAndSessionSeq(id, sessionSeq);
        for (FullDialog fullDialog : dialogHistory) {
            fullDialog.getFileList().forEach(item -> {
                fullDialog.getFileNameList().add(item.getFileName());
                fullDialog.getFileUrlList().add(item.getFileUrl());
            });
        }
        return dialogHistory;
    }

    @Override
    public DialogRespDto getTextToTextResp(String userId, Integer sessionSeq, String content) {
        QueryWrapper<Dialog> wrapper = new QueryWrapper<>();
        QueryWrapper<Dialog> queryWrapper = wrapper.eq("user_id", userId)
                .eq("session_seq", sessionSeq)
                .not(item -> item.eq("role","image"))
                .orderByAsc("dialog_seq");
        List<Dialog> dialogs = baseMapper.selectList(queryWrapper);
        // 提高上下文
        List<ChatMessage> chatMessageList = new ArrayList<>();
        dialogs.forEach(dialog -> chatMessageList.add(
                new ChatMessage(dialog.getRole().getName().toLowerCase(),dialog.getContent())));

        GptModel gptModel = GptModel.builder().openai_model("gpt-3.5-turbo").openai_chatMessageList(chatMessageList)
                .openai_temperature(0.5).openai_maxToken(2048).openai_topP(1D).build();
        GptModelBridge gptModelBridge = new GptModelBridge(openaiGptModel);
        String ans = gptModelBridge.generateTextToTextResp(gptModel);

        String createTime = TimeUtils.getTimeNow();
        int dialogSeq = getDialogCountByUserId(userId);
        Dialog dialog = Dialog.builder().content(ans).sessionSeq(sessionSeq)
                .role(DialogRole.ASSISTANT)
                .createTime(createTime).dialogSeq(dialogSeq).userId(userId).build();
        save(dialog);
        DialogRespDto dialogRespDto = new DialogRespDto();
        dialogRespDto.setContent(ans);
        return dialogRespDto;
    }

}




