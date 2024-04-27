package com.myworld.gradution_project_backend.Controller;

import com.myworld.gradution_project_backend.DTO.DialogDto;
import com.myworld.gradution_project_backend.DTO.DialogRespDto;
import com.myworld.gradution_project_backend.VO.FullDialog;
import com.myworld.gradution_project_backend.bean.Dialog;
import com.myworld.gradution_project_backend.exception.GptModelException;
import com.myworld.gradution_project_backend.service.DialogService;
import com.myworld.gradution_project_backend.service.FileService;
import com.myworld.gradution_project_backend.utils.GPTKit;
import com.myworld.gradution_project_backend.utils.JWTUtils;
import com.myworld.gradution_project_backend.utils.Result;
import com.myworld.gradution_project_backend.utils.TimeUtils;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.myworld.gradution_project_backend.utils.Constants.*;

/**
 * @ClassName GPTController
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/3/25 15:07
 * @Version 1.0
 */
@RestController
public class GPTController {
    @Autowired
    private FileService fileService;
    @Autowired
    private DialogService dialogService;

    @PostMapping("/getDialogHistory")
    public Result getDialogHistory(@RequestBody Dialog dialog){
        String id = dialog.getUserId();
        Integer sessionSeq = dialog.getSessionSeq();
        List<FullDialog> dialogHistory = dialogService.getDialogHistoryByUserIdAndSessionSeq(id,sessionSeq);
        List<DialogDto> result = new ArrayList<>();
        dialogHistory.forEach(item -> {
            DialogDto dialogDto = new DialogDto();
            BeanUtils.copyProperties(item,dialogDto);
            result.add(dialogDto);
        });
        return Result.success().put(result);
    }

    /**
     *
     * @param file
     * @param content
     * @param sessionSeq
     * @param isImageMode 判断当前请求是否跟图片生成有关
     * @param req
     * @return
     */
    @Transactional
    @PostMapping("/saveDialog")
    public Result saveDialog(@RequestParam(name = "file", required = false) MultipartFile[] file,
                              @RequestParam String content, @RequestParam Integer sessionSeq,
                              @RequestParam String isImageMode, HttpServletRequest req) {
        String jwtToken = req.getHeader("token");
        String userId = JWTUtils.getTokenInfo(jwtToken).getClaim("userId").asString();
        boolean isImageReq = Boolean.parseBoolean(isImageMode);
        // 保存对话
        Dialog dialog = dialogService.saveDialog(content,sessionSeq,userId, isImageReq);
        // 将文件写到本地并保存相关数据到数据库
        DialogRespDto dialogRespDto = fileService.saveFile(file, dialog.getId());

        return Result.success().put("clientData", dialogRespDto);
    }

    @PostMapping("/getDialogResp")
    public Result getDialogResp(@RequestBody FullDialog fullDialog) throws TencentCloudSDKException,
            InterruptedException, IOException {
        String userId = fullDialog.getUserId();
        Integer sessionSeq = fullDialog.getSessionSeq();
        String content = fullDialog.getContent();
        String isImageMode = fullDialog.getIsImageMode();
        List<String> fileUrlList = fullDialog.getFileUrlList();

        boolean isImageReq = Boolean.parseBoolean(isImageMode);
        DialogRespDto dialogRespDto = null;
        if (!isImageReq){
            dialogRespDto = dialogService.getTextToTextResp(userId,sessionSeq,content);
        }else if (fileUrlList.size() == 0){
            dialogRespDto = fileService.getTextToImageResp(userId,sessionSeq, content);
        }else {
            dialogRespDto = fileService.getImageToImageResp(userId,sessionSeq,content, fileUrlList);
        }
        return Result.success().put("data",dialogRespDto);
    }

    @RequestMapping(value = "/image/{imageDirctory}/{imageName}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("imageDirctory") String imageDirctory,
                                           @PathVariable("imageName") String imageName) {
        return fileService.getImg(imageDirctory, imageName);
    }
}
