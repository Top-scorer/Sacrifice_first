package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserComment {
    private Integer id;
    private Integer videoId;
    private String username;
    private String image;
    private String content;
    private String translation; //翻译
    private Short sentimentScore; //情感极性
    private Double sentimentProb; //极性概率
    private LocalDateTime createTime; //创建时间
}
