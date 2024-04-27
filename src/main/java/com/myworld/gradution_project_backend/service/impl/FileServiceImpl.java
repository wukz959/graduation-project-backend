package com.myworld.gradution_project_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myworld.gradution_project_backend.DTO.DialogRespDto;
import com.myworld.gradution_project_backend.Enum.DialogRole;
import com.myworld.gradution_project_backend.VO.FullDialog;
import com.myworld.gradution_project_backend.VO.GptModel;
import com.myworld.gradution_project_backend.bean.Dialog;
import com.myworld.gradution_project_backend.bean.File;
import com.myworld.gradution_project_backend.exception.GptModelException;
import com.myworld.gradution_project_backend.service.DialogService;
import com.myworld.gradution_project_backend.service.FileService;
import com.myworld.gradution_project_backend.mapper.FileMapper;
import com.myworld.gradution_project_backend.utils.GPTKit;
import com.myworld.gradution_project_backend.utils.JWTUtils;
import com.myworld.gradution_project_backend.utils.TimeUtils;
import com.myworld.gradution_project_backend.web.GptModelBridge;
import com.myworld.gradution_project_backend.web.service.AllGptModel;
import com.myworld.gradution_project_backend.web.service.impl.OpenaiGptModel;
import com.myworld.gradution_project_backend.web.service.impl.TencentGptModel;
import com.tencentcloudapi.aiart.v20221229.AiartClient;
import com.tencentcloudapi.aiart.v20221229.models.*;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.myworld.gradution_project_backend.utils.Constants.*;

/**
 * @author wkz
 * @description 针对表【file(文件表-目前保存图片，后续可添加字段表示其他类型文件)】的数据库操作Service实现
 * @createDate 2024-03-27 16:35:09
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
        implements FileService {

    @Autowired
    private DialogService dialogService;
    @Autowired
    private GPTKit gptKit;
    @Autowired
    private OpenaiGptModel openaiGptModel;

    @Value("${tencent-gpt.credential.id}")
    private String secretId;
    @Value("${tencent-gpt.credential.key}")
    private String secretKey;

    private Credential credential;

    @PostConstruct
    private void init() {
        credential = new Credential(secretId, secretKey);
    }

    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 4,
            0, TimeUnit.SECONDS, new ArrayBlockingQueue(100));

    @Override
    public File saveImg(MultipartFile file) {
        File fileEntity = new File();
        //把文件保存的方法抽离出来了
        Map<String, String> map = uploadFile(file);
        String fileOriginalName = file.getOriginalFilename();
        String fileName = map.get("fileName");
        String fileUrl = map.get("fileUrl");
        Long fileSize = file.getSize();

        fileEntity.setFileName(fileName);
        fileEntity.setFileUrl(fileUrl);
        fileEntity.setFileOriginName(fileOriginalName);
        fileEntity.setFileSize(fileSize);

        return fileEntity;
    }

    @Override
    public ResponseEntity<byte[]> getImg(String imageDirctory, String imageName) {
        byte[] imageContent = null;
        String path = "src/main/resources/" + imageDirctory + "/" + imageName;
        try {
            imageContent = fileToByte(new java.io.File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
    }

    @Override
    public DialogRespDto saveFile(MultipartFile[] file, Integer dialogId) throws GptModelException {
        String createTime = TimeUtils.getTimeNow();
        DialogRespDto dialogRespDto = new DialogRespDto();
        File fileEntity;
        List<File> fileList = new ArrayList<>();
        if (file != null) {
            for (int i = 0; i < file.length; i++) {
                MultipartFile multipartFile = file[i];
                fileEntity = saveImg(multipartFile);
                fileEntity.setDialogId(dialogId);
                fileEntity.setCreateTime(createTime);
                fileList.add(fileEntity);
                dialogRespDto.getFileNameList().add(fileEntity.getFileName());
                dialogRespDto.getFileUrlList().add(fileEntity.getFileUrl());
            }
        }
        saveBatch(fileList);
        return dialogRespDto;
    }

    @Override
    public String getAndSaveGptResp(String id, Integer sessionSeq, String content, String[] fileUrl, HttpServletRequest req) {
        // 目前dall-e-3只支持生成一张图片；且只有腾讯的大模型支持根据图生成图
        // cloudflare的ai 网关接受请求的参数不能带有换行符\n
        String question = content.replaceAll("\n", "  ");

        String jwtToken = req.getHeader("token");
        String userId = JWTUtils.getTokenInfo(jwtToken).getClaim("userId").asString();
        String createTime = TimeUtils.getTimeNow();

        List<FullDialog> dialogHistory = dialogService.getDialogHistoryByUserIdAndSessionSeq(userId, sessionSeq);
        Integer dialogSeq = dialogHistory.size() + 1;
        // 记录问题与相关信息
        Dialog dialog = Dialog.builder().userId(userId).sessionSeq(sessionSeq).content(question)
                .dialogSeq(dialogSeq).createTime(createTime).build();
        // 给GPT提供上下文
        List<ChatMessage> chatMessageList = dialogHistory.stream()
                .map(item -> new ChatMessage(item.getRole().toString(), item.getContent())).collect(Collectors.toList());

        ChatMessage chatMessage = gptKit.talkToChatGPT(chatMessageList, question);
        String ans = chatMessage.getContent();

//        dialogService.saveDialog(question, sessionSeq, req);

//            ans = "你好！GPT模型的最大token数量取决于具体的模型架构和预训练过程。在GPT-3这样的较大模型中，最大token数量为2048个。\n但需要注意，进行预测或生成回答时，建议将输入文本限制在模型的最大输入限制范围内，以确保获得最佳的性能和效果。对于GPT-3模型而言，通常建议将输入限制在最大token数量的一半左右，即约1024个tokens。这样做可以避免模型的响应时间过长和性能下降。";
//            ans = "生活不易，全靠演戏\\n\\n生活是一部戏，每个人都在自己的舞台上扮演着不同的角色。有时我们是欢笑的主角，有时又是悲伤的配角。在这个舞台上，我们需要扮演好各种人物，迎接生活中的各种挑战。\\n\\n生活的不易令人感叹，我们时常被现实的压力所困扰。工作上的竞争激烈，学业上的压力重重，人际关系的纷争，物质的追逐等等，都让我们感到疲惫和迷茫。在这个时候，我们需要学会“演戏”。\\n\\n演戏并不是说我们要做伪装和虚假，而是学会扮演不同的角色，去面对和解决生活中的各种问题。就像在舞台上扮演角色一样，我们需要找到自己的定位和方式，展现最好的一面，同时也要注意保护自己的内心，不被困扰所侵蚀。\\n\\n演戏也是一种修炼，我们需要学会换位思考。当我们遇到困境和挫折时，换个角色去看问题，或许就能找到更好的解决方法。用不同的“角色”去看待生活，可以让我们更加客观和理智地面对问题，减轻生活的压力。\\n\\n此外，演戏还可以让我们拥有更强的适应能力和沟通能力。在与人交往的过程中，能够灵活地切换角色，理解他人的立场和需求，将有助于建立良好的人际关系。生活中充满了各种各样的人物，我们需要学会与不同的人相处，处理好彼此之间的关系。\\n\\n生活不易，全靠演戏。只有懂得演戏，我们才能在这个舞台上活得更";
        // 保存回答
        dialog.setContent(ans);
        dialogService.save(dialog);
        return null;
    }

    /**
     * 通过文本生成图片并将相关数据生成对话保存到数据库
     *
     * @param userId
     * @param sessionSeq
     * @param content
     * @return 响应数据，包含生成的图片所在相对路径，如：/upload2024-3/xxx.jpg
     * @throws TencentCloudSDKException
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DialogRespDto getTextToImageResp(String userId, Integer sessionSeq, String content) throws GptModelException {
//        GptModelBridge gptModelBridge = new GptModelBridge(tencentGptModel);
//        GptModel gptModel = GptModel.builder().tencent_logoAdd(0L)
//                .tencent_prompt(content).tencent_style("xieshi").build();

        GptModelBridge gptModelBridge = new GptModelBridge(openaiGptModel);
        GptModel gptModel = GptModel.builder()
                .openai_model("dall-e-3").openai_prompt(content)
                .openai_n(1).openai_size("1024x1024").build();
        String[] imageUrls = gptModelBridge.generateTextToImageResp(gptModel);

        //保存gpt的回答，由于只响应了图片，所以content为""
        Dialog dialog = dialogService.saveDialog("", sessionSeq, userId,true);
        String timeNow = TimeUtils.getTimeNow();

        //通过url下载图片，并保存到本地
        List<String> imgsDir = downloadImageFromUrls(imageUrls);
        List<File> fileResult = new ArrayList<>();
        DialogRespDto dialogRespDto = new DialogRespDto();

        for (String imgDir : imgsDir) {
            String imgName = imgDir.substring(imgDir.lastIndexOf("/") + 1);
            File file = File.builder().fileName(imgName).fileOriginName("gptGenerateWithNoName.jpg")
                    .fileUrl(imgDir).dialogId(dialog.getId()).createTime(timeNow).build();
            fileResult.add(file);
            dialogRespDto.getFileUrlList().add(imgDir);
        }
        //保存对话对应的图片
        saveBatch(fileResult);
        return dialogRespDto;
    }

    /**
     * 通过图片生成图片，目前接口只支持接收一张图片，以及生成一张图片
     * @param userId
     * @param sessionSeq
     * @param content
     * @param fileUrlList
     * @return
     * @throws IOException
     * @throws TencentCloudSDKException
     */
    @Override
    public DialogRespDto getImageToImageResp(String userId, Integer sessionSeq, String content, List<String> fileUrlList) throws IOException, TencentCloudSDKException {
        // 生成图片请求并得到图片响应
        AiartClient client = new AiartClient(credential, "ap-guangzhou");
        ImageToImageRequest req = new ImageToImageRequest();
        String imgBase64Src = convertImageToBase64(IMG_SAVE_DIRECTORY + fileUrlList.get(0));
        req.setInputImage(imgBase64Src);
        req.setPrompt(content);
        req.setLogoAdd(0L);
        req.setStrength(0.7f);
        ImageToImageResponse resp = client.ImageToImage(req);
        String resultImage = resp.getResultImage(); //得到的默认是base64编码的图片

        //保存gpt的回答
        Dialog dialog = dialogService.saveDialog("", sessionSeq, userId, true);

        // 将Base64编码的图片字符串解码为字节数组
        byte[] imageBytes = Base64.getDecoder().decode(resultImage);
        String imgName = IMG_PREFIX + UUID.randomUUID().toString().replaceAll("-", "") + IMG_SUFFIX;
        String imgDir = generateDateFileSavePath();
        String imagePath = IMG_SAVE_DIRECTORY + imgDir + imgName;

        // 将字节数组保存为图片文件，并保存到本地
        FileOutputStream fos = new FileOutputStream(imagePath);
        fos.write(imageBytes);
        fos.close();

        //保存文件信息到数据库
        String timeNow = TimeUtils.getTimeNow();
        File file = File.builder().fileName(imgName).fileOriginName("gptGenerateWithNoName.jpg")
                .fileUrl(imgDir + imgName).dialogId(dialog.getId()).createTime(timeNow).build();
        save(file);
        DialogRespDto dialogRespDto = new DialogRespDto();
        dialogRespDto.getFileUrlList().add(imgDir + imgName);

        System.out.println("图片保存成功！");
        return dialogRespDto;
    }


    private String convertImageToBase64(String imagePath) throws IOException {
        java.io.File file = new java.io.File(imagePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        fileInputStream.close();
        byteArrayOutputStream.close();

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    // 文件上传，保存到本地,如果需要直接修改本方法进行文件上传
    private Map<String, String> uploadFile(MultipartFile file) {
        // 这个map用于返回imageUrl，imageId之类的数据
        Map<String, String> map = new HashMap<>();
        try {

            String fileRealName = file.getOriginalFilename();
            String suffix = fileRealName.substring(fileRealName.lastIndexOf(".") + 1);  //后缀  jpg  png
            //解决文件名字问题：使用uuid的方式;
            String fileName = IMG_PREFIX + UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;

            String fileDir = generateDateFileSavePath() + fileName;
            file.transferTo(new java.io.File("src/main/resources/" + fileDir).getAbsoluteFile());

            map.put("fileName", fileName);
            map.put("fileUrl", fileDir);  //传到前端的url，例如： /upload2022-5/xxx.jpg

        } catch (IOException e) {
            throw new GptModelException("upload file error: " + e.getMessage());
        }
        return map;
    }

    // 将文件转为字节流
    private byte[] fileToByte(java.io.File img) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage bi;
            bi = ImageIO.read(img);
            ImageIO.write(bi, "png", baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baos.close();
        }
        return bytes;
    }

    /**
     * @param urls
     * @return 返回图片所在目录，如：/upload2024-3/xxx.jpg
     */
    private List<String> downloadImageFromUrls(String[] urls) {
        List<String> fileUrls = new ArrayList<>();
        for (String url : urls) {
//            String destinationPath = "src/main/resources/image.jpg"; // 要保存的图片文件路径
            String fileName = IMG_PREFIX + UUID.randomUUID().toString().replaceAll("-", "") + IMG_SUFFIX;
            String fileDir = generateDateFileSavePath() + fileName;
            String destinationPath = IMG_SAVE_DIRECTORY + fileDir; // 要保存的图片文件路径
            fileUrls.add(fileDir);
            try {
                // 打开URL连接
                URL source = new URL(url);
                InputStream inputStream = source.openStream();

                // 从URL读取图片数据并保存到本地文件系统
                Path path = Paths.get(destinationPath);

                Files.copy(inputStream, path);
                System.out.println("图片下载成功！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileUrls;
    }

    /**
     * 创建文件实际保存的目录，并返回文件相对保存路径
     *
     * @return 如：upload2024-3/。但实际保存目录应为：src/main/resources/upload2024-3/
     */
    private String generateDateFileSavePath() {
        //设置文件的目录，按照月份进行分类：
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.YEAR);
        String yearMonth = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1);
        String filePath = IMG_SAVE_DIRECTORY + "upload" + yearMonth + "/"; //项目路径+年月份
        // 创建文件目录
        java.io.File realPath = new java.io.File(filePath);  //上传文件保存地址：realPath = "E:\\图片保存\\upload\\2022年5月"
        if (!realPath.exists()) {
            boolean mkdirs = realPath.mkdirs();//创建文件目录，可以创建多层目录
        }
        return "upload" + yearMonth + "/";
    }

}




