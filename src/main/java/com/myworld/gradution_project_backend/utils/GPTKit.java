package com.myworld.gradution_project_backend.utils;

import com.theokanning.openai.OpenAiApi;
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
import java.util.List;

import static com.theokanning.openai.service.OpenAiService.*;

/**
 * @ClassName GPTKit
 * @Author wkz
 * @Date 2023/12/5 11:17
 * @Version 1.0
 */
@Component
public class GPTKit {
    @Value("${openai-gpt.host}")
    private String host;
    @Value("{openai-gpt.token}")
    private String token;

    //将设置的代理传给OpenAiService即可
    private OpenAiService service;

    // @Value用的是set方法，而成员变量初始化在set方法之前，导致其他成员变量会获取到null值，@PostConstruct用于在依赖注入完成后执行初始化操作
    @PostConstruct
    public void init(){
        OkHttpClient client = defaultClient(token, Duration.ofSeconds(10000))
                .newBuilder()
                .build();
        Retrofit retrofit = defaultRetrofit(client, defaultObjectMapper())
                .newBuilder()
                .baseUrl(host)
                .build();
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        service = new OpenAiService(api);
    }


    public ChatMessage talkToChatGPT(List<ChatMessage> chatMessageList, String question)
    {
        CreateImageRequest createImageRequest = CreateImageRequest.builder()
                .model("dall-e-3")
                .prompt("A cute baby sea otter")
                .n(1)
                .size("1024x1024")
//                .user("testing")
                .build();

        List<Image> images = service.createImage(createImageRequest).getData();
        images.get(0).getRevisedPrompt();
        String url = images.get(0).getUrl();

        //TODO
        String ans = "tmp";
//        chatMessageList.add(new ChatMessage(ChatMessageRole.USER.value(), question));
//        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
//                .model(model)
//                .messages(chatMessageList)
//                .temperature(0.5)
//                .maxTokens(maxTokens)
//                .topP(1D)
//                .build();
//        List<ChatCompletionChoice> completionChoiceList = service.createChatCompletion(chatCompletionRequest).getChoices();
//        String ans = completionChoiceList.get(0).getMessage().getContent();
        return new ChatMessage(ChatMessageRole.ASSISTANT.value(), ans);
    }
}
