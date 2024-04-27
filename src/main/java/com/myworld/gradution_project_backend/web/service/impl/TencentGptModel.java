package com.myworld.gradution_project_backend.web.service.impl;

import com.myworld.gradution_project_backend.VO.GptModel;
import com.myworld.gradution_project_backend.exception.GptModelException;
import com.myworld.gradution_project_backend.web.service.AllGptModel;
import com.tencentcloudapi.aiart.v20221229.AiartClient;
import com.tencentcloudapi.aiart.v20221229.models.QueryTextToImageProJobRequest;
import com.tencentcloudapi.aiart.v20221229.models.QueryTextToImageProJobResponse;
import com.tencentcloudapi.aiart.v20221229.models.SubmitTextToImageProJobRequest;
import com.tencentcloudapi.aiart.v20221229.models.SubmitTextToImageProJobResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName TencentGptModel
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/4/11 16:33
 * @Version 1.0
 */
@Component
public class TencentGptModel implements AllGptModel {

    @Value("${tencent-gpt.credential.id}")
    private String secretId;
    @Value("${tencent-gpt.credential.key}")
    private String secretKey;

    private Credential credential;

    @PostConstruct
    private void init() {
        credential = new Credential(secretId, secretKey);
    }

    @Override
    public String[] getTextToImageResp(GptModel gptModel) throws GptModelException {
        String[] imageUrls;
        try {
            // 创建请求，获取jobId
            AiartClient client = new AiartClient(credential, "ap-guangzhou");
            SubmitTextToImageProJobRequest textReq = new SubmitTextToImageProJobRequest();
            textReq.setPrompt(gptModel.getTencent_prompt());
            textReq.setLogoAdd(gptModel.getTencent_logoAdd());
            textReq.setStyle(gptModel.getTencent_style());

            SubmitTextToImageProJobResponse resp = client.SubmitTextToImageProJob(textReq);

            String jobId = resp.getJobId();
            System.out.println("jobId: " + jobId);

            QueryTextToImageProJobRequest queryTextToImage = new QueryTextToImageProJobRequest();
            queryTextToImage.setJobId(jobId); // 通过textToImage拿到JobId

            /**
             * 通过jobId拿到图片url
             * 由于图片生成需要时间，所以这里需要轮询接口查看图片是否生成完成。
             * 注意：看文档说是JobStatusCode值为7时为处理完成，5是处理失败，但实际运行起来可发现返回值均是5，且图片的url也成功返回
             */
            QueryTextToImageProJobResponse textToImageResp = client.QueryTextToImageProJob(queryTextToImage);
            int cnt = 0; // 轮询次数
            while (!textToImageResp.getJobStatusCode().equals("5")
                    && !textToImageResp.getJobStatusCode().equals("7") && cnt < 16) {
                System.out.println("statusCode: " + textToImageResp.getJobStatusCode() + "   cnt: " + cnt);
                cnt++;
                Thread.sleep(500);
                textToImageResp = client.QueryTextToImageProJob(queryTextToImage);
            }
            if (cnt >= 16) {
                throw new GptModelException("tencent gpt model exception: 轮询查找超时" );
            }
            if (textToImageResp.getJobStatusCode().equals("5")) {
                System.out.println("生成失败: " + textToImageResp.getJobStatusCode());
            } else {
                System.out.println("排队成功" + textToImageResp.getJobStatusCode());
            }
            imageUrls = textToImageResp.getResultImage();
        } catch (TencentCloudSDKException | InterruptedException e) {
            throw new GptModelException("tencent gpt model exception: " + e.getMessage());
        }
        return imageUrls;
    }
}
