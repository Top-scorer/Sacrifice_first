package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RandomVideo { //selectVideo相同封装
    private Integer videoId;
    private String videoUrl;
    private String videoTitle;
    private String category;
    private String authorUsername;
    private boolean followed;
    private boolean liked;
    private boolean collected;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private LocalDateTime updateTime;
}
