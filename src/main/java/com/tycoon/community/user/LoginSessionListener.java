package com.tycoon.community.user;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Tomcat이 세션을 종료할 때 호출하는 수신기입니다. 로그인 Map에 남은 세션을 정리합니다.
 */

public class LoginSessionListener implements HttpSessionListener {

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(event.getSession().getServletContext());
		if (context != null) {
			context.getBean(LoginSessions.class).remove(event.getSession());
		}
	}
}
