package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchVideoResult {
    private Integer videoId;
    private String videoUrl;
    private String coverUrl;
    private String videoTitle;
    private String category;
    private String authorUsername;
    private String image;
    private boolean followed;
    private boolean liked;
    private boolean collected;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
}
