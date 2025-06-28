package com.fzb.controller;

import com.fzb.pojo.Result;
import com.fzb.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class UploadController {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    /*@PostMapping("/upload")
    public Result upload(MultipartFile image) throws Exception {
        log.info("文件上传，文件名：{}", image.getOriginalFilename());
        //调用阿里云OSS工具类
        String url = aliOSSUtils.upload(image);
        log.info("文件上传完成,文件访问的url：{}",url);
        return Result.success(url);
    }*/

    @PostMapping("/uploadvideo")
    public Result uploadVideo(MultipartFile video) throws Exception {
        String url = aliOSSUtils.upload(video, "video");
        log.info("url:{}", url);
        return Result.success(url);
    }

    @PostMapping("/uploadcover")
    public Result uploadCover(MultipartFile cover) throws Exception {
        String url = aliOSSUtils.upload(cover, "cover");
        log.info("url:{}", url);
        return Result.success(url);
    }

    @PostMapping("/uploadimage")
    public Result uploadImage(MultipartFile image) throws Exception {
        String url = aliOSSUtils.upload(image, "image");
        log.info("url:{}", url);
        return Result.success(url);
    }

}