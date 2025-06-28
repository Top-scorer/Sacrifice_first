package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage {
    private Integer id;
    private String username;
    private String image;
    private String content;
    private LocalDateTime createTime; //创建时间
}
