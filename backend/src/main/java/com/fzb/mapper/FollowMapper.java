package com.fzb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FollowMapper {
    @Select("SELECT follow_id FROM follow WHERE fans_id = #{userId}")
    List<Long> selectFollowingIds(@Param("userId") Long userId);
}