package com.tycoon.community.common;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * 컨트롤러에서 발생한 예외를 모아 응답 상태와 오류 화면 또는 로그인 이동을 결정합니다.
 */

@ControllerAdvice // 내부 예외/비밀번호/SQL을 사용자 화면에 노출하지 않습니다.
@Slf4j
public class CommonExceptionAdvice {

	@ExceptionHandler(GameException.class)
	public String game(GameException ex, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (ex.getStatus() == 401) {
			// 일반 페이지 이동만 로그인 화면으로 안내합니다. fetch에는 401을 유지합니다.
			String accept = request.getHeader("Accept");
			if ("GET".equals(request.getMethod()) && accept != null && accept.contains("text/html")) {
				model.asMap().clear();
				return "redirect:/member/login?expired=1";
			}
			model.addAttribute("loginRequired", true);
		}
		response.setStatus(ex.getStatus());
		model.addAttribute("message", ex.getMessage());
		return "error";
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String notFound(Model model) {
		model.addAttribute("message", "요청한 페이지를 찾을 수 없습니다.");
		return "error";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String other(Exception ex, HttpServletResponse response, Model model) {
		log.error("Request failed", ex);
		response.setStatus(500);
		model.addAttribute("message", "처리하지 못했어요. DB 연결과 서버 로그를 확인한 뒤 다시 시도해 주세요.");
		return "error";
	}
}
