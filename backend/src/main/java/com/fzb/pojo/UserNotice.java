package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotice {
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime createTime; //创建时间
}
