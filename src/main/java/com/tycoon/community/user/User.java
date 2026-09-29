package com.tycoon.community.user;

import lombok.Data;
import lombok.ToString;

/**
 * 로그인 계정 한 행을 담습니다. loginId은 로그인 아이디, role은 계정 권한입니다.
 */

@Data
public class User {
	private Long userId;
	private String loginId;
	private String role;
	@ToString.Exclude
	private String password;
}
