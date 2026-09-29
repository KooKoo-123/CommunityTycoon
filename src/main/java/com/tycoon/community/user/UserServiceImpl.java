package com.tycoon.community.user;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tycoon.community.common.GameException;
import com.tycoon.community.tycoon.TycoonMapper;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import com.tycoon.community.tycoon.TycoonService;

/**
 * 회원 입력값과 중복 아이디를 확인하고 회원 생성·로그인 확인·프로필 변경을 처리합니다.
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserMapper memberMapper;
	private final TycoonMapper tycoonMapper;
	private final TycoonService tycoonService;

	@Transactional
	@Override
	public User register(String loginId, String nickname, String password) {
		if (loginId == null || !loginId.matches("[a-zA-Z0-9_]{4,30}")) {
			throw new GameException("아이디는 영문, 숫자, 밑줄 4~30자로 입력해 주세요.");
		}
		if (nickname == null || nickname.trim().isEmpty() || nickname.trim().length() > 30) {
			throw new GameException("운영자 닉네임은 1~30자로 입력해 주세요.");
		}
		if (password == null || password.length() < 8 || password.length() > 20) {
			throw new GameException("비밀번호는 8~20자로 입력해 주세요.");
		}
		User member = new User();
		member.setLoginId(loginId);
		// 원문 대신 매번 다른 salt를 사용하는 BCrypt 해시를 저장합니다.
		member.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
		try {
			memberMapper.insert(member);
		} catch (DuplicateKeyException ex) {
			throw new GameException("이미 사용 중인 아이디입니다.");
		}
		// 회원만 생기고 사이트가 없는 상황을 막기 위해 두 INSERT를 같은 트랜잭션으로 처리합니다.
		tycoonMapper.insert(member.getUserId(), nickname.trim());
		return member;
	}

	@Override
	// BCrypt로 확인합니다. 이전 버전의 평문 계정은 첫 정상 로그인 때 전환합니다.
	@Transactional
	public User login(String loginId, String password) {
		User member = memberMapper.findByLoginId(loginId);
		if (member == null
				|| password == null
				|| password.length() > 20
				|| !matchesPassword(member, password)) {
			throw new GameException("아이디 또는 비밀번호를 확인해 주세요.");
		}
		return member;
	}

    private boolean matchesPassword(User member, String password) {
        String stored = member.getPassword();
        if (stored == null) return false;
        if (stored.length() > 20) {
            try { return BCrypt.checkpw(password, stored); }
            catch (IllegalArgumentException invalidHash) { return false; }
        }
        // 기존 스키마의 평문(최대 20자)에만 적용하는 일회성 호환 경로입니다.
        if (!java.security.MessageDigest.isEqual(
                stored.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                password.getBytes(java.nio.charset.StandardCharsets.UTF_8))) return false;
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
        memberMapper.updatePassword(member.getUserId(), hashed);
        member.setPassword(hashed);
        return true;
    }

	@Override
	public User get(long userId) {
		return memberMapper.findByUserId(userId);
	}

	@Override
	public void updateProfile(long userId, String nickname, String theme) {
		tycoonService.requireModule(userId, "mypage");
		if (nickname == null || nickname.trim().isEmpty() || nickname.trim().length() > 30) {
			throw new GameException("닉네임은 1~30자로 입력해 주세요.");
		}
		if (!"sky".equals(theme) && !"forest".equals(theme)) throw new GameException("테마를 확인해 주세요.");
		if ("forest".equals(theme)) tycoonService.requireModule(userId, "theme");
		tycoonMapper.updateProfile(userId, nickname.trim(), theme);
	}
}
