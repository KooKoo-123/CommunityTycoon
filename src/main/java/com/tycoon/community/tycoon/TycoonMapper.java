package com.tycoon.community.tycoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis SQL 호출 목록입니다. resources/mapper/TycoonMapper.xml의 namespace는 이 인터페이스, id는 메서드 이름에 대응합니다. @Param 이름은 SQL의 #{이름}과 연결됩니다.
 */

@Mapper
public interface TycoonMapper {

	int insert(@Param("userId") long userId, @Param("userNickname") String userNickname);

	GameData findByUser(long userId);

	int update(GameData site);

	int updateProfile(@Param("userId") long userId,
			@Param("userNickname") String userNickname,
			@Param("userTheme") String userTheme);

	int updateProfileImage(@Param("userId") long userId,
			@Param("userProfileImageId") long userProfileImageId);
}
