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

    void updateProfileImage(
            @Param("userId") Long userId,
            @Param("profileImageUrl") String profileImageUrl,
            @Param("profileImagePublicId") String profileImagePublicId
    );

    void deleteProfileImage(@Param("userId") Long userId);

    // OAuth
    int countByNickname(@Param("nickname") String nickname);

    int insertGoogleUser(User user);



    // TODO 이메일 중복(int), 닉네임 중복(int), insert


}