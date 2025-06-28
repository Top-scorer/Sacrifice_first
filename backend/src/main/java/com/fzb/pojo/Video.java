package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    private Integer id;
    private Integer authorId;
    private String videoUrl;
    private String coverUrl;
    private String videoTitle;
    private Short status ;
    private Integer likeBase;//基础点赞量
    private String category;
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //修改时间
}
