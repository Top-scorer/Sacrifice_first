package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserResult {
    private Integer userId;
    private String username;
    private String image;
    private boolean followed;
    private String followCount;
    private String fansCount;
}
