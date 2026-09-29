package com.tycoon.community.user;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import com.tycoon.community.common.GameException;
import lombok.RequiredArgsConstructor;

/**
 * 회원 번호마다 현재 유효한 세션 하나를 보관합니다. DB 게임 저장소가 아니라 로그인 확인용 Map입니다.
 */

@Component
@RequiredArgsConstructor
public class LoginSessions {

	private final Map<Long, HttpSession> sessions = new HashMap<>();

	public synchronized void login(long userId, HttpSession session) {
		HttpSession old = sessions.get(userId);
		if (old != null && old != session) {
			try {
				old.invalidate();
			} catch (IllegalStateException ignored) {
			}
		}
		session.setAttribute("userId", userId);
		session.setMaxInactiveInterval(30 * 60);
		sessions.put(userId, session);
	}

	public synchronized long require(HttpSession session) {
		try {
			Long id = (Long) session.getAttribute("userId");
			if (id != null && sessions.get(id) == session) {
				return id;
			}
		} catch (IllegalStateException ignored) {
			// 다른 곳에서 로그인해 무효화된 세션입니다.
		}
		// Tomcat이 복원한 세션도 메모리의 로그인 기록과 다르면 다시 로그인해야 합니다.
		try { session.invalidate(); }
		catch (IllegalStateException ignored) { /* 이미 만료된 세션입니다. */ }
		throw new GameException(401, "로그인이 만료되었거나 다른 곳에서 로그인했습니다. 다시 로그인해 주세요.");
	}

	public synchronized void remove(HttpSession session) {
		Long found = null;
		for (Map.Entry<Long, HttpSession> entry : sessions.entrySet()) {
			if (entry.getValue() == session) {
				found = entry.getKey();
				break;
			}
		}
		if (found != null) {
			sessions.remove(found);
		}
	}
}
