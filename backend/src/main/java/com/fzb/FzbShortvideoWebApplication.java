package com.fzb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan//开启Servlet组件的支持
@SpringBootApplication
public class FzbShortvideoWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FzbShortvideoWebApplication.class, args);
    }

}
