package com.myworld.gradution_project_backend.service;

import com.myworld.gradution_project_backend.DTO.DialogRespDto;
import com.myworld.gradution_project_backend.bean.File;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myworld.gradution_project_backend.exception.GptModelException;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
* @author wkz
* @description 针对表【file(文件表-目前保存图片，后续可添加字段表示其他类型文件)】的数据库操作Service
* @createDate 2024-03-27 16:35:09
*/
public interface FileService extends IService<File> {
    File saveImg(MultipartFile file);

    ResponseEntity<byte[]> getImg(String imageDirctory, String imageName);

    DialogRespDto saveFile(MultipartFile[] file, Integer dialogId) throws GptModelException;

    String getAndSaveGptResp(String id, Integer sessionSeq, String content, String[] fileUrl, HttpServletRequest req);

    DialogRespDto getTextToImageResp(String userId, Integer sessionSeq, String content) throws TencentCloudSDKException, InterruptedException, GptModelException;

    DialogRespDto getImageToImageResp(String userId, Integer sessionSeq, String content, List<String> fileUrlList) throws IOException, TencentCloudSDKException;
}
