package com.fzb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String image;       // 头像URL
    private Integer followCount; // 关注数
    private Integer fansCount;   // 粉丝数
    private boolean followed;   //是否关注
    private String signature;   //签名
    private Integer gender;     //性别
}
