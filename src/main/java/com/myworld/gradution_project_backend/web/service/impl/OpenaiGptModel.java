package com.myworld.gradution_project_backend.web.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myworld.gradution_project_backend.DTO.DialogRespDto;
import com.myworld.gradution_project_backend.VO.GptModel;
import com.myworld.gradution_project_backend.exception.GptModelException;
import com.myworld.gradution_project_backend.web.service.AllGptModel;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.theokanning.openai.service.OpenAiService.*;

/**
 * @ClassName OpenaiGptModel
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/4/11 17:33
 * @Version 1.0
 */
@Component
public class OpenaiGptModel implements AllGptModel {
    @Value("${openai-gpt.host}")
    private String host;
    @Value("${openai-gpt.token}")
    private String token;

    private OpenAiService service;

    @PostConstruct
    private void init(){
        //需要额外设置一个能访问chatGPT的魔法访问代理
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, Duration.ofSeconds(10000))
                .newBuilder()
                .build();
        Retrofit retrofit = defaultRetrofit(client, mapper)
                .newBuilder()
                .baseUrl(host)
                .build();
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        //将设置的代理传给OpenAiService即可
        service = new OpenAiService(api);
    }

    @Override
    public String getTextAnsByText(GptModel gptModel) {
        String model = gptModel.getOpenai_model();
        List<ChatMessage> chatMessageList = gptModel.getOpenai_chatMessageList();
        Double temperature = gptModel.getOpenai_temperature();
        Integer maxToken = gptModel.getOpenai_maxToken();
        Double topP = gptModel.getOpenai_topP();

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(chatMessageList)
                .temperature(temperature)
                .maxTokens(maxToken)
                .topP(topP)
                .build();
        List<ChatCompletionChoice> completionChoiceList = service.createChatCompletion(chatCompletionRequest).getChoices();
        String ans = completionChoiceList.get(0).getMessage().getContent();

        return ans;
    }

    @Override
    public String[] getTextToImageResp(GptModel gptModel){
        String model = gptModel.getOpenai_model();
        Integer n = gptModel.getOpenai_n();
        String prompt = gptModel.getOpenai_prompt();
        String size = gptModel.getOpenai_size();
        CreateImageRequest createImageRequest = CreateImageRequest.builder()
                .model(model)
                .prompt(prompt)
                .n(n)
                .size(size)
                .build();

        List<Image> images = service.createImage(createImageRequest).getData();
        String url = images.get(0).getUrl();
        return new String[]{url};
    }
}
