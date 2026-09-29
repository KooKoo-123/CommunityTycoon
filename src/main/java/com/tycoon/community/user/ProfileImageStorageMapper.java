package com.tycoon.community.user;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis SQL 호출 목록입니다. resources/mapper/ProfileImageStorageMapper.xml의 namespace는 이 인터페이스, id는 메서드 이름에 대응합니다. @Param 이름은 SQL의 #{이름}과 연결됩니다.
 */

@Mapper
public interface ProfileImageStorageMapper {

	void insert(ProfileImageStorage profileImageStorage);

	List<ProfileImageStorage> list(long userId);

	ProfileImageStorage find(@Param("profileImageStorageId") long profileImageStorageId, @Param("userId") long userId);

	int delete(@Param("profileImageStorageId") long profileImageStorageId, @Param("userId") long userId);
}
