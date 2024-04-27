package com.myworld.gradution_project_backend;

import com.myworld.gradution_project_backend.utils.TimeUtils;
import com.tencentcloudapi.aiart.v20221229.AiartClient;
import com.tencentcloudapi.aiart.v20221229.models.*;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.profile.Language;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static com.myworld.gradution_project_backend.utils.Constants.*;

/**
 * @ClassName TencentImageTest
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/4/7 12:05
 * @Version 1.0
 */
@SpringBootTest
public class TencentImageTest {
    @Test
    public void testTencentCreateImageApi()
    {
//        try {
//            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId，SecretKey。
//            // 为了保护密钥安全，建议将密钥设置在环境变量中或者配置文件中，请参考本文凭证管理章节。
//            // 硬编码密钥到代码中有可能随代码泄露而暴露，有安全隐患，并不推荐。
//            Credential cred = new Credential("AKIDYw74mGWXCrLcmofOzPgmyOlEeAiFvRlr",
//                    "DJNwKWUEoEVfotkcMsGWaVDhy2tXyCD1");
////            Credential cred = new Credential(System.getenv("TENCENTCLOUD_SECRET_ID"), System.getenv("TENCENTCLOUD_SECRET_KEY"));
//
//            // 实例化一个http选项，可选的，没有特殊需求可以跳过
//            HttpProfile httpProfile = new HttpProfile();
//            // 从3.0.96版本开始, 单独设置 HTTP 代理
//            // httpProfile.setProxyHost("真实代理ip");
//            // httpProfile.setProxyPort(真实代理端口);
//            httpProfile.setReqMethod("GET"); // get请求(默认为post请求)
//            httpProfile.setConnTimeout(30); // 请求连接超时时间，单位为秒(默认60秒)
//            httpProfile.setWriteTimeout(30);  // 设置写入超时时间，单位为秒(默认0秒)
//            httpProfile.setReadTimeout(30);  // 设置读取超时时间，单位为秒(默认0秒)
//            httpProfile.setEndpoint("cvm.ap-shanghai.tencentcloudapi.com"); // 指定接入地域域名(默认就近接入)
//
//            // 实例化一个client选项，可选的，没有特殊需求可以跳过
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setSignMethod(ClientProfile.SIGN_TC3_256); // 指定签名算法(默认为TC3-HMAC-SHA256)
//            // 自3.1.80版本开始，SDK 支持打印日志。
//            clientProfile.setHttpProfile(httpProfile);
//            clientProfile.setDebug(true);
//            // 从3.1.16版本开始，支持设置公共参数 Language, 默认不传，选择(ZH_CN or EN_US)
//            clientProfile.setLanguage(Language.EN_US);
//            // 实例化要请求产品(以cvm为例)的client对象,clientProfile是可选的
//            CvmClient client = new CvmClient(cred, "ap-shanghai", clientProfile);
//
//
//
//            // 实例化一个cvm实例信息查询请求对象,每个接口都会对应一个request对象。
//            DescribeInstancesRequest req = new DescribeInstancesRequest();
//
//            // 填充请求参数,这里request对象的成员变量即对应接口的入参
//            // 您可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
//            Filter respFilter = new Filter(); // 创建Filter对象, 以zone的维度来查询cvm实例
//            respFilter.setName("zone");
//            respFilter.setValues(new String[] { "ap-shanghai-1", "ap-shanghai-2" });
//            req.setFilters(new Filter[] { respFilter }); // Filters 是成员为Filter对象的列表
//            // 通过client对象调用DescribeInstances方法发起请求。注意请求方法名与请求对象是对应的
//            // 返回的resp是一个DescribeInstancesResponse类的实例，与请求对象对应
//            DescribeInstancesResponse resp = client.DescribeInstances(req);
//
//            CreateImageRequest createImageRequest = new CreateImageRequest();
//            createImageRequest.setImageName("test");
//            createImageRequest.setImageDescription("一件纯白衬衫，无其他图案");
//
//            CreateImageResponse response = client.CreateImage(createImageRequest);
//
//
//            // 输出json格式的字符串回包
//            System.out.println(DescribeInstancesResponse.toJsonString(resp));
//
//            // 也可以取出单个值。
//            // 您可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
//            System.out.println(resp.getTotalCount());
//        } catch (TencentCloudSDKException e) {
//            System.out.println(e.toString());
//        }
    }
    @Test
    public void test1()
    {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential("AKIDYw74mGWXCrLcmofOzPgmyOlEeAiFvRlr", "DJNwKWUEoEVfotkcMsGWaVDhy2tXyCD1");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            // 推荐使用北极星，相关指引可访问如下链接
            // https://git.woa.com/tencentcloud-internal/tencentcloud-sdk-java#%E5%8C%97%E6%9E%81%E6%98%9F
            httpProfile.setEndpoint("aiart.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            AiartClient client = new AiartClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            ImageToImageRequest req = new ImageToImageRequest();
            req.setPrompt("美化");
            String src = convertImageToBase64("src/main/resources/upload2024-3/beauty.jpg");
            req.setInputImage(src);
            req.setLogoAdd(0L);
            req.setRspImgType("url");
            // 返回的resp是一个ImageToImageResponse的实例，与请求对象对应
            ImageToImageResponse resp = client.ImageToImage(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));
            System.out.println(" **************************              ------------------");
            System.out.println(resp.getResultImage());
        } catch (TencentCloudSDKException | IOException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public String testBase64() throws IOException {
        String src = convertImageToBase64("src/main/resources/upload2024-3/beauty.jpg");
//        System.out.println(src);
        return src;
    }
    public  String convertImageToBase64(String imagePath) throws IOException {
        File file = new File(imagePath);
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
    @Test
    public void testBase64ToImage(String imagePath) throws IOException {
        String base64Image = testBase64(); // Base64编码的图片字符串
//        String imagePath = "src/main/resources/beauty.jpg"; // 要保存的图片文件路径

        try {
            // 将Base64编码的图片字符串解码为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // 将字节数组保存为图片文件
            FileOutputStream fos = new FileOutputStream(imagePath);
            fos.write(imageBytes);
            fos.close();
            System.out.println("图片保存成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void textToImage() // 文本生成图片，但resp得到的是JobId，后续要通过下面的testGetTextToImage方法来获取图片url
    {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential("AKIDYw74mGWXCrLcmofOzPgmyOlEeAiFvRlr", "DJNwKWUEoEVfotkcMsGWaVDhy2tXyCD1");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            // 推荐使用北极星，相关指引可访问如下链接
            // https://git.woa.com/tencentcloud-internal/tencentcloud-sdk-java#%E5%8C%97%E6%9E%81%E6%98%9F
            httpProfile.setEndpoint("aiart.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            AiartClient client = new AiartClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SubmitTextToImageProJobRequest req = new SubmitTextToImageProJobRequest();
            req.setPrompt("女生，一米七五");
            // 返回的resp是一个SubmitTextToImageProJobResponse的实例，与请求对象对应
            SubmitTextToImageProJobResponse resp = client.SubmitTextToImageProJob(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
    @Test
    public void testGetTextToImage(){
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential("AKIDYw74mGWXCrLcmofOzPgmyOlEeAiFvRlr", "DJNwKWUEoEVfotkcMsGWaVDhy2tXyCD1");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            // 推荐使用北极星，相关指引可访问如下链接
            // https://git.woa.com/tencentcloud-internal/tencentcloud-sdk-java#%E5%8C%97%E6%9E%81%E6%98%9F
            httpProfile.setEndpoint("aiart.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            AiartClient client = new AiartClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            QueryTextToImageProJobRequest req = new QueryTextToImageProJobRequest();
            req.setJobId("1317069457-1712582106-047b7679-f5aa-11ee-8d15-5254007430c7-0"); // 通过textToImage拿到JobId
            // 返回的resp是一个QueryTextToImageProJobResponse的实例，与请求对象对应
            QueryTextToImageProJobResponse resp = client.QueryTextToImageProJob(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void testDownloadImageFromUrl()
    {
//        String imageUrl = "https://hellowukz.oss-cn-guangzhou.aliyuncs.com/Blogs/img-1703211181179-23875.png"; // 图片URL
        String imageUrl = "https://aiart-1258344699.cos.ap-guangzhou.myqcloud.com/text_to_img_pro/1317069457/1317069457-1712554954-ccb8d76d-f56a-11ee-8d15-5254007430c7-0/1?q-sign-algorithm=sha1&q-ak=AKIDpRovliU1IJ5ctufBSVIq8AwTlnZ5MN8d&q-sign-time=1712554962%3B1712558562&q-key-time=1712554962%3B1712558562&q-header-list=host&q-url-param-list=&q-signature=17e83a7893d14cba430481bfeb6bc99cdb7b2d66"; // 图片URL
        String destinationPath = "src/main/resources/hhh.jpg"; // 要保存的图片文件路径

        try {
            // 打开URL连接
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();

            // 从URL读取图片数据并保存到本地文件系统
            // 使用Files.copy将输入流中的内容保存到本地文件
            Path path = Paths.get(destinationPath);

            Files.copy(inputStream, path);
            System.out.println("图片下载成功！");
            System.out.println(path.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test()
    {
        String tmp = "/upload2020-3/sad456- 45.jpg";
        System.out.println(tmp.substring(tmp.lastIndexOf("/") + 1));
    }
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1,1,
            0, TimeUnit.SECONDS,new ArrayBlockingQueue(10));
    @Test
    public void testThreadPool() throws InterruptedException {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        boolean tmp = false;
        ScheduledFuture<?> scheduledFuture = scheduledThreadPool.scheduleAtFixedRate(() -> {
            int cnt = 0;
            while (cnt < 4) {
                cnt++;
                System.out.println(TimeUtils.getTimeNow() + ": " + Thread.currentThread().getName() + ": " + cnt);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        scheduledFuture.cancel(true);

        scheduledThreadPool.scheduleAtFixedRate(()->{
            int cnt = 0;
            while (cnt<4)
            {
                cnt++;
                System.out.println(TimeUtils.getTimeNow() + ": " + Thread.currentThread().getName() + ": " + cnt);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return;
        },0,1,TimeUnit.SECONDS);

//        for (int i = 0; i < 4; i++) {
//            poolExecutor.submit(()->{
//                int cnt = 0;
//                while (cnt<4)
//                {
//                    cnt++;
//                    System.out.println(Thread.currentThread().getName() + ": " + cnt);
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
        Thread.sleep(100000);

    }

    @Test
    public void testImageToImage()
    {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential("AKIDYw74mGWXCrLcmofOzPgmyOlEeAiFvRlr", "DJNwKWUEoEVfotkcMsGWaVDhy2tXyCD1");

            // 实例化要请求产品的client对象,clientProfile是可选的
            AiartClient client = new AiartClient(cred, "ap-guangzhou");
            // 实例化一个请求对象,每个接口都会对应一个request对象
            ImageToImageRequest req = new ImageToImageRequest();
            String imgBase64Src = convertImageToBase64("src/main/resources/upload2024-3/beauty.jpg");
            req.setInputImage(imgBase64Src);
            req.setPrompt("美化人物");
            req.setLogoAdd(0L);
            req.setStrength(0.7f);

            // 返回的resp是一个ImageToImageResponse的实例，与请求对象对应
            ImageToImageResponse resp = client.ImageToImage(req);
            String resultImage = resp.getResultImage();
            // 将Base64编码的图片字符串解码为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(resultImage);
            String imgName = "img-" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
            String imagePath = "src/main/resources/" + imgName;
            // 将字节数组保存为图片文件
            FileOutputStream fos = new FileOutputStream(imagePath);
            fos.write(imageBytes);
            fos.close();
            System.out.println("图片保存成功！");
            System.out.println(resultImage);

        } catch (TencentCloudSDKException | IOException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void downloadImageFromUrls() {

        String url = "https://aiart-1258344699.cos.ap-guangzhou.myqcloud.com/text_to_img_pro/1317069457/1317069457-1712742031-5f0ee1c2-f71e-11ee-8d15-5254007430c7-0/1?q-sign-algorithm=sha1&q-ak=AKIDpRovliU1IJ5ctufBSVIq8AwTlnZ5MN8d&q-sign-time=1712742037%3B1712745637&q-key-time=1712742037%3B1712745637&q-header-list=host&q-url-param-list=&q-signature=6f59080fc644fb0b16211df3bd4cda9885017fc8";

//            String destinationPath = "src/main/resources/image.jpg"; // 要保存的图片文件路径
        String fileName = "img-" + UUID.randomUUID().toString().replaceAll("-", "") + IMG_SUFFIX;
        String fileDir = generateDateFileSavePath() + fileName;
        String destinationPath = IMG_SAVE_DIRECTORY + fileDir; // 要保存的图片文件路径
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
