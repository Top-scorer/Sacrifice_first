package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSentimentProfile {
    private double averageSentiment; // 平均情感分(0-4)
    private String dominantEmotion; // 主导情感类别
}