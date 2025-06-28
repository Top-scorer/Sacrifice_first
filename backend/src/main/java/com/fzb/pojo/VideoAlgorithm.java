package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoAlgorithm {
    private Long total;
    private List videoSentiment;
    private List typeSentiment;
}
