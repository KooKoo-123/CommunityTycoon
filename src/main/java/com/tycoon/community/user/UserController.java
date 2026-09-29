package com.tycoon.community.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tycoon.community.common.GameException;
import com.tycoon.community.tycoon.TycoonService;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tycoon.community.common.ViewData;

/**
 * 로그인·가입·마이페이지 요청을 받습니다. GET은 화면, POST는 입력한 자료의 처리를 담당합니다.
 */

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService memberService;
	private final TycoonService tycoonService;
	private final LoginSessions loginSessions;
	private final ViewData viewData;
	private final ProfileImageStorageMapper profileImageStorageMapper;

	@GetMapping("/login")
	public String login(HttpServletRequest request, Model model) {
		viewData.prepare(request, model);
		return "member/login";
	}

	@GetMapping("/join")
	public String join(HttpServletRequest request, Model model) {
		viewData.prepare(request, model);
		return "member/join";
	}

	@PostMapping("/login")
	public String login(
			@RequestParam String loginId,
			@RequestParam String password,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {
		try {
			User member = memberService.login(loginId, password);
			establish(request, member.getUserId());
			Cookie cookie = new Cookie("tycoonTheme", "forest".equals(tycoonService.get(member.getUserId()).getUserTheme()) ? "forest" : "sky");
			cookie.setPath("/");
			cookie.setMaxAge(60 * 60 * 24 * 365);
			response.addCookie(cookie);
			return "redirect:/tycoon";
		} catch (GameException ex) {
			viewData.prepare(request, model);
			model.addAttribute("message", ex.getMessage());
			model.addAttribute("loginId", loginId);
			return "member/login";
		}
	}

	@PostMapping("/join")
	public String join(
			@Valid JoinRequestDTO dto,
			BindingResult bindingResult,
			HttpServletRequest request,
			Model model) {
		viewData.prepare(request, model);
		model.addAttribute("loginId", dto.getLoginId());
		model.addAttribute("nickname", dto.getNickname());
		if (bindingResult.hasErrors()) {
			model.addAttribute("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
			return "member/join";
		}
		try {
			memberService.register(dto.getLoginId(), dto.getNickname(), dto.getPassword());
			model.asMap().clear();
			return "redirect:/member/login";
		} catch (GameException exception) {
			model.addAttribute("message", exception.getMessage());
			return "member/join";
		}
	}

	private void establish(HttpServletRequest req, long id) {
		HttpSession old = req.getSession(false);
		if (old != null) old.invalidate();
		loginSessions.login(id, req.getSession(true));
	}

	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/member/login";
	}

	@GetMapping("/mypage")
	public String mypage(HttpSession session, HttpServletRequest request, Model model) {
		if (session.getAttribute("userId") == null) return "redirect:/member/login";
		long userId = loginSessions.require(session);
		viewData.prepare(request, model);
		model.addAttribute("profileImageStorageList", profileImageStorageMapper.list(userId));
		return "member/mypage";
	}

	@GetMapping("/manager")
	public String manager(HttpSession session, HttpServletRequest request, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) return "redirect:/member/login";
		tycoonService.requireModule(userId, "mypage");
		loginSessions.require(session);
		viewData.prepare(request, model);
		return "member/manager";
	}

	@PostMapping("/profile")
	public String profile(HttpSession session, @RequestParam String nickname,
			@RequestParam String theme, HttpServletResponse response,
			RedirectAttributes redirect) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) throw new GameException(401, "로그인해 주세요.");
		loginSessions.require(session);
		memberService.updateProfile(userId, nickname, theme);
		Cookie cookie = new Cookie("tycoonTheme", theme);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 365);
		response.addCookie(cookie);
		redirect.addFlashAttribute("notice", "프로필을 변경했어요.");
		return "redirect:/member/mypage";
	}
}
