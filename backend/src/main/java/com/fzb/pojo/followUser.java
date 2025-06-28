package com.fzb.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class followUser {//fansUser同样封装
    private String image;
    private String followCount;
    private String fansCount;
    private String username;
}
