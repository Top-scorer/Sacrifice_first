package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SentimentResult {
    private Short sentimentScore; // 预测类别（0-4
    private Double sentimentProb; //概率
}
