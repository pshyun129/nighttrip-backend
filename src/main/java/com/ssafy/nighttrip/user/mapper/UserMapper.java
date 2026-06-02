package com.ssafy.nighttrip.user.mapper;

import com.ssafy.nighttrip.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByEmail(@Param("email") String email);

    User findById(@Param("userId") Long userId);
}