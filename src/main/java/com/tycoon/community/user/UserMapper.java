package com.tycoon.community.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis SQL 호출 목록입니다. resources/mapper/UserMapper.xml의 namespace는 이 인터페이스, id는 메서드 이름에 대응합니다. @Param 이름은 SQL의 #{이름}과 연결됩니다.
 */

@Mapper // MyBatis가 XML SQL과 연결하는 DAO 역할입니다.
public interface UserMapper {

	int updatePassword(@Param("userId") long userId, @Param("password") String password);

	User findByLoginId(String loginId);

	User findByUserId(long userId);

	int insert(User member);
}
