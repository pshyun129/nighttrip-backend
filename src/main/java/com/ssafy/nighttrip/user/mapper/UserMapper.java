package com.ssafy.nighttrip.user.mapper;

import com.ssafy.nighttrip.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByEmail(@Param("email") String email);

    User findById(@Param("userId") Long userId);

    void updateMyInfo(
            @Param("userId") Long userId,
            @Param("nickname") String nickname
    );

    void deleteMyCourse(@Param("userId") Long userId);

    int deleteMyInfo(@Param("userId") Long userId);

    void updateMyPassword(
            @Param("userId") Long userId,
            @Param("password") String password
    );



}