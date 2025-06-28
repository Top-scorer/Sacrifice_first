package com.fzb;

import com.fzb.pojo.SentimentResult;
import com.fzb.utils.AliOSSUtils;
import com.fzb.utils.NlpPipeline;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;



@Slf4j
@SpringBootTest
class FzbShortvideoWebApplicationTests {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    /**
     * 本地文件上传到OSS
     * @throws Exception
     */
    @Test
    public void upload() throws Exception {
        File localFile = new File("C:\\Users\\ZhiboFan\\Desktop\\20200117215058_pwcgf.jpg");

        // 转换为 MultipartFile
        FileInputStream inputStream = new FileInputStream(localFile);
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                localFile.getName(),
                "image/jpg",
                inputStream
        );
        String url = aliOSSUtils.upload(multipartFile, "image");
        log.info(url);
    }


    // stanford coreNLP英文情感分析
    static NlpPipeline nlpPipeline = null;
    public static void processText(String text) {
        SentimentResult sentimentResult = new SentimentResult();
        sentimentResult = nlpPipeline.estimatingSentiment(text);
    }
    @Test
    public void sentimentuse() {
        String text = "What a lovely puppy";
        nlpPipeline  = new NlpPipeline();
        nlpPipeline.init();
        processText(text);
    }



}
