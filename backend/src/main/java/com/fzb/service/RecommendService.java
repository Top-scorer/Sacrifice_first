package com.fzb.service;

import com.fzb.pojo.PageBean;

import java.util.List;

public interface RecommendService {


    public PageBean recommendVideos(Long userId, Integer page, Integer pageSize);
}