package com.myworld.gradution_project_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myworld.gradution_project_backend.VO.FullDialog;
import com.myworld.gradution_project_backend.bean.Dialog;
import com.myworld.gradution_project_backend.bean.User;
import com.myworld.gradution_project_backend.utils.JWTUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.*;

/**
 * @ClassName TestTmp
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/5/8 13:31
 * @Version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TestTmp {
    @Autowired
    private MockMvc mockMvc;
    private static String tmpToken;
    private ObjectMapper objectMapper = new ObjectMapper();
    @BeforeAll
    static void init()
    {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("username","testName");
        hashMap.put("userId","1788099830964256769");
        tmpToken = JWTUtils.getToken(hashMap);
        System.out.println("init");
    }
    @Test
    public void mockControllerTest() throws Exception {
        //请求的url地址
        String url = "/hello";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("token",tmpToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("hello"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
    @Test
    public void mockLogin() throws Exception {
        //请求的url地址
        String url = "/user/login";
        User user = new User();
        user.setUsername("testName");
        user.setPassword("123456");
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("token",tmpToken)
                .content(objectMapper.writeValueAsString(user))
//                .param("username","testName")
//                .param("password","123456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("{token:}"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void testGptControllerGetDialogHistory() throws Exception {
        //请求的url地址
        String url = "/getDialogHistory";
        Dialog dialog = new Dialog();
        dialog.setUserId("1788099830964256769");
        dialog.setSessionSeq(0);
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("token",tmpToken)
                .content(objectMapper.writeValueAsString(dialog))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void testGptControllerGetDialogRespTextToText() throws Exception {
        //请求的url地址
        String url = "/getDialogResp";
        FullDialog fullDialog = new FullDialog();
        fullDialog.setUserId("1788099830964256769");
        fullDialog.setSessionSeq(0);
//        fullDialog.setContent("白色衬衫");
        fullDialog.setIsImageMode("false");
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("token",tmpToken)
                .content(objectMapper.writeValueAsString(fullDialog))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void testGptControllerGetDialogRespTextToImage() throws Exception {
        //请求的url地址
        String url = "/getDialogResp";
        FullDialog fullDialog = new FullDialog();
        fullDialog.setUserId("1788099830964256769");
        fullDialog.setSessionSeq(0);
        fullDialog.setContent("白色衬衫");
        fullDialog.setIsImageMode("true");
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("token",tmpToken)
                .content(objectMapper.writeValueAsString(fullDialog))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void testGptControllerGetDialogRespImageToImage() throws Exception {
        //请求的url地址
        String url = "/getDialogResp";
        FullDialog fullDialog = new FullDialog();
        fullDialog.setUserId("1788099830964256769");
        fullDialog.setSessionSeq(0);
        fullDialog.setContent("优化一下");
        fullDialog.setIsImageMode("true");
        fullDialog.setFileUrlList(Collections.singletonList("upload2024-5/file-8b61b03e4a9d41f3a42afa35ca572afc.jpg"));
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("token",tmpToken)
                .content(objectMapper.writeValueAsString(fullDialog))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void testGptControllerGetImage() throws Exception {
        //请求的url地址
        String url = "/image/upload2024-5/file-aae26e51ba134bf09e113e9e697f055c.jpg";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.IMAGE_PNG)
                .header("token",tmpToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_PNG))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
    @Test
    public void testGptControllerSaveDialog() throws Exception {
        //请求的url地址
        String content = "单元测试";
        String url = "/saveDialog";
        int sessionSeq = 0;
        String isImageMode = "false";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("token",tmpToken)
                .param("content",content)
                .param("sessionSeq", Integer.toString(sessionSeq))
                .param("isImageMode",isImageMode)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void test()
    {
        int a = 1;
        int b = 0;
        int res = a + b;
        Assertions.assertEquals(1,res);
    }
    public void throwExce(){
        throw new NullPointerException();
    }
    @Test
    public void testException(){
        Assertions.assertThrows(NullPointerException.class, this::throwExce);
    }

}
