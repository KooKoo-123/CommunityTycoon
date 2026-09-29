package com.tycoon.community.board;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tycoon.community.common.GameException;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.tycoon.community.user.LoginSessions;

/**
 * 댓글 작성·수정·삭제 요청을 받아 서비스에 전달하고 게시글 상세 화면으로 돌아갑니다.
 */

@Controller
@RequestMapping("/board/{boardId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
	private final CommentService commentService;
	private final LoginSessions loginSessions;

	private long userId(HttpSession session) {
		return loginSessions.require(session);
	}

	@PostMapping
	public String create(
			HttpSession session, @PathVariable long boardId, @RequestParam String content) {
		commentService.create(userId(session), boardId, content);
		return "redirect:/board/" + boardId + "#comments";
	}

	@PostMapping("/{commentId}/edit")
	public String edit(
			HttpSession session,
			@PathVariable long boardId,
			@PathVariable long commentId,
			@RequestParam String content) {
		commentService.edit(userId(session), boardId, commentId, content);
		return "redirect:/board/" + boardId + "#comments";
	}

	@PostMapping("/{commentId}/delete")
	public String delete(
			HttpSession session, @PathVariable long boardId, @PathVariable long commentId) {
		commentService.delete(userId(session), boardId, commentId);
		return "redirect:/board/" + boardId + "#comments";
	}
}
