package com.tycoon.community.user;

/**
 * 회원 가입·로그인과 사용자 프로필 변경을 제공하는 서비스 계약입니다.
 */

public interface UserService {

	void updateProfile(long userId, String nickname, String theme);

	User register(String loginId, String nickname, String password);

	User login(String loginId, String password);

	User get(long userId);
}
