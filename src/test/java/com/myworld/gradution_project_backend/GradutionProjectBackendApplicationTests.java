package com.myworld.gradution_project_backend;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myworld.gradution_project_backend.DTO.DialogDto;
import com.myworld.gradution_project_backend.Enum.DialogRole;
import com.myworld.gradution_project_backend.VO.FullDialog;
import com.myworld.gradution_project_backend.bean.Dialog;
import com.myworld.gradution_project_backend.service.DialogService;
import com.myworld.gradution_project_backend.utils.JWTUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.profile.Language;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageEditRequest;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.CreateImageVariationRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Retrofit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import static com.myworld.gradution_project_backend.utils.Constants.SIGNING_KEY;
import static com.theokanning.openai.service.OpenAiService.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GradutionProjectBackendApplicationTests {
    @Test
    void contextLoads() {
        HashMap<String, Object> map = new HashMap<>();

        Calendar instance = Calendar.getInstance();
        // 20秒后令牌token失效
        instance.add(Calendar.SECOND,60);

        String token = JWT.create()
                .withHeader(map) // header可以不写，因为默认值就是它
                .withClaim("username", "wukz")  //payload
                .withClaim("password", "123456")
                .withExpiresAt(instance.getTime()) // 指定令牌的过期时间
                .sign(Algorithm.HMAC256(SIGNING_KEY));//签名

        System.out.println(token);
    }
    @Test
    public void test(){
        // 通过签名生成验证对象
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SIGNING_KEY)).build();

        DecodedJWT verify = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IjEyMzQ1NiIsImV4cCI6MTcxMDc0MjUyMSwidXNlcm5hbWUiOiJ3dWt6In0.wIuG4JZCGVDW_IxC4UA9x677c0Tpj3h4lYfvfrGN918");
        System.out.println(verify.getClaim("password"));
        System.out.println(verify.getClaim("username"));
        System.out.println("令牌过期时间："+verify.getExpiresAt());
    }
    @Test
    public void test2(){
        HashMap<String,String> data = new HashMap();
        data.put("username","wukz");
        data.put("password","987354");
        String token = JWTUtils.getToken(data);
        System.out.println(token);
        System.out.println(JWTUtils.verify(token));
        DecodedJWT tokenInfo = JWTUtils.getTokenInfo(token);
        System.out.println(tokenInfo.getClaim("username"));
        System.out.println(tokenInfo.getClaim("password"));
    }
    @Test
    public void test4(){
//        File file = new File("src/main/resources");
//        System.out.println(file.getAbsoluteFile());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = dateFormat.format(new Date());
        System.out.println(format);
    }
    String host = "https://chatmyworld.fun";
    Integer port = 80;
    String token = "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";
    //需要额外设置一个能访问chatGPT的魔法访问代理
    ObjectMapper mapper = defaultObjectMapper();
    //        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    OkHttpClient client = defaultClient(token, Duration.ofSeconds(10000))
            .newBuilder()
//                .proxy(proxy)
            .build();
    Retrofit retrofit = defaultRetrofit(client, mapper)
            .newBuilder()
            .baseUrl(host)
            .build();
    OpenAiApi api = retrofit.create(OpenAiApi.class);
    //将设置的代理传给OpenAiService即可
    OpenAiService service = new OpenAiService(api);
    List<ChatMessage> chatMessageList = new ArrayList<>();
    public String sendMessage(String message)
    {
        chatMessageList.add(new ChatMessage(ChatMessageRole.USER.value(), message));
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(chatMessageList)
                .temperature(0.5)
                .maxTokens(2048)
                .topP(1D)
                .build();
        List<ChatCompletionChoice> completionChoiceList = service.createChatCompletion(chatCompletionRequest).getChoices();
        String ans = completionChoiceList.get(0).getMessage().getContent();
        chatMessageList.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(), ans));
        return ans;
    }
    @Test
    public void testGpt() throws InterruptedException {
//        ChatMessage chatMessage = new ChatMessage("system",
//                "你现在叫Amy，之后我都将以此称呼你,你也得以此自称,之后我有可能改变你的名字，也请你到时以新的名字自称");
//        ChatMessage question = new ChatMessage("user", "你是谁？");
//        ChatMessage gptMsg = new ChatMessage("assistant", "你好，我是你的智能助手，你可以叫我Amy。有什么问题我可以帮助你解答吗？");
//        ChatMessage qeustionAgain = new ChatMessage("user", "我刚刚有向你提问过，我要测试一下你的历史功能，复述一下我之前对你的提问，以及你对我刚刚的提问所进行的回答。如若你不知道则回答没有即可");
//
//        chatMessageList.add(chatMessage);
//        chatMessageList.add(question);
//        chatMessageList.add(gptMsg);
//        chatMessageList.add(qeustionAgain);
        chatMessageList.add(new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "你现在叫Amy,之后我都将以此称呼你"));
        chatMessageList.add(new ChatMessage(ChatMessageRole.USER.value(),"我叫Mike，记住了吗？"));
        chatMessageList.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(),"是的，我记住了，你叫Mike。有什么问题或者需要帮助的吗，Mike？"));
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.print("输入: ");
            String next = scanner.next();
            System.out.println("GPT回答：" + sendMessage(next));;
        }

    }

    @Test
    public void test5(){
        DialogRole[] values = DialogRole.values();
        System.out.println(Arrays.toString(values));
        DialogRole assistant = DialogRole.valueOf("ASSISTANT");
//        System.out.println(DialogRole.USER.ordinal());
        System.out.println(assistant);
    }
    @Autowired
    private DialogService dialogService;
    @Test
    public void test6(){
        List<FullDialog> dialogHistory = dialogService.getDialogHistoryByUserIdAndSessionSeq("1770052389202669569",1);
        dialogHistory.forEach(item -> {
            DialogDto dialogDto = new DialogDto();
            BeanUtils.copyProperties(item, dialogDto);
            System.out.println(dialogDto);
            System.out.println(" ***************** ");
//            System.out.println(item);
            System.out.println();
        });
//        List<Dialog> dialogList = dialogService.query().list();
//        dialogList.forEach(System.out::println);
    }
    @Test
    public void testImgGeneration()
    {
        CreateImageRequest createImageRequest = CreateImageRequest.builder()
                .model("dall-e-3")
                .prompt("Create a plain white short sleeve with the words' 你好 'on it, without any additional background or pattern")
                .n(1)
                .size("1024x1024")
                .build();

        List<Image> images = service.createImage(createImageRequest).getData();
        String url = images.get(0).getUrl();
        System.out.println(images);
        System.out.println(" -------------------------------- ");
        System.out.println(url);
        System.out.println(" *********************** ");
        System.out.println(images.get(0).getB64Json());
    }
    @Test
    public void testDownloadSaveImg(){
        String imageUrl = "https://oaidalleapiprodscus.blob.core.windows.net/private/org-cEZgkCw51EGks9sfncX0sTUq/user-DQmcJfhnNAhYhfq4TGVXHolA/img-UGEn3DAnIRrtT0JA9fEfnkTp.png?st=2024-04-05T02%3A33%3A57Z&se=2024-04-05T04%3A33%3A57Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2024-04-04T23%3A43%3A10Z&ske=2024-04-05T23%3A43%3A10Z&sks=b&skv=2021-08-06&sig=YuSsPk/VskZjWHAgByOTBb7CwmmBw5fKYSLqBY2lx%2Bw%3D";

        try {
            URL url = new URL(imageUrl);
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();
            File file = new File("src/main/resources/" + "test.png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            int bytesum = 0;
            int byteread;
            byte[] buffer = new byte[1024];
            while ((byteread = inputStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                fileOutputStream.write(buffer, 0, byteread);
            }
            fileOutputStream.close();
            System.out.println("图片下载成功");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("图片下载失败: " + e.getMessage());
        }
    }
    @Test
    public void createImageEditWithMask(){
        CreateImageEditRequest createImageRequest = CreateImageEditRequest.builder()
                .prompt("a penguin with a red hat")
                .responseFormat("url")
                .size("256x256")
                .user("testing")
                .n(2)
                .build();

//        List<Image> images = service.createImageEdit(createImageRequest,
//                new File("src/main/resources/upload2024-3/beauty.jpg"), maskPath).getData();

    }

    @Test
    public void testGptImageVaration()
    {
//        CreateImageVariationRequest.builder().
    }

    @Test
    public void testEnum(){
        QueryWrapper<Dialog> wrapper = new QueryWrapper<>();
        QueryWrapper<Dialog> queryWrapper = wrapper.eq("user_id", "1770052389202669569")
                .not(item -> item.eq("role","image"))
                .orderByDesc("dialog_seq").last("limit 1");
        Dialog lastDialog = dialogService.getBaseMapper().selectOne(queryWrapper);
        DialogRole nowRole = (lastDialog==null || lastDialog.getRole() == DialogRole.ASSISTANT )? DialogRole.USER : DialogRole.ASSISTANT;
        System.out.println(lastDialog);
        System.out.println(nowRole);
        System.out.println(DialogRole.USER == DialogRole.SYSTEM);
    }
    @Test
    public void test1()
    {
        Boolean tmp = null;
        System.out.println(tmp||true);
        System.out.println(tmp||false);
    }
    @Test
    public void test3()
    {
//        QueryWrapper<Dialog> wrapper = new QueryWrapper<>();
//        QueryWrapper<Dialog> queryWrapper = wrapper.eq("user_id", "1770052389202669569")
//                .eq("session_seq", 0)
//                .not(item -> item.eq("role","image"))
//                .orderByAsc("dialog_seq");
//        List<Dialog> dialogs = dialogService.getBaseMapper().selectList(queryWrapper);
////        dialogs.forEach(System.out::println);
//        System.out.println(dialogs.get(0).getRole());
//        System.out.println(dialogs.get(0).getRole().getName());
        DialogRole user = DialogRole.USER;
        System.out.println(user.getName());
    }
}
