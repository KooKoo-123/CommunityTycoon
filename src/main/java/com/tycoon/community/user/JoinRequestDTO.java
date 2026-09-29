package com.tycoon.community.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

/**
 * 회원가입 폼 값을 받습니다. 필드의 검증 어노테이션은 @Valid와 함께 입력 조건을 검사합니다.
 */

@Data
public class JoinRequestDTO {

	@NotBlank(message = "아이디는 영문, 숫자, 밑줄 4~30자로 입력해 주세요.")
	@Pattern(regexp = "[a-zA-Z0-9_]{4,30}", message = "아이디는 영문, 숫자, 밑줄 4~30자로 입력해 주세요.")
	private String loginId;

	@NotBlank(message = "닉네임을 입력해 주세요.")
	private String nickname;

	@NotNull(message = "비밀번호는 8~20자로 입력해 주세요.")
	@Size(min = 8, max = 20, message = "비밀번호는 8~20자로 입력해 주세요.")
	@ToString.Exclude
	private String password;
}
