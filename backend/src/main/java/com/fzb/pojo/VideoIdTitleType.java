package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoIdTitleType {
    private Integer id;
    private String title;
    public String type;
}
