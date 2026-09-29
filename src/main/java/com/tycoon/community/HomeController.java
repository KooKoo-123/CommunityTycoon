package com.tycoon.community;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 첫 접속 주소(/)를 받아 로그인 여부에 따라 로그인 화면 또는 게임으로 보냅니다.
 */

@Controller
public class HomeController {

	@GetMapping({"/", "/index"})
	public String home(HttpSession session) {
		return session.getAttribute("userId") == null
				? "redirect:/member/login"
				: "redirect:/tycoon";
	}
}
