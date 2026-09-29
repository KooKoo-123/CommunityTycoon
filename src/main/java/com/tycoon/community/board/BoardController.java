package com.tycoon.community.board;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tycoon.community.common.GameException;
import com.tycoon.community.tycoon.TycoonService;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import com.tycoon.community.common.ViewData;
import com.tycoon.community.user.LoginSessions;
import java.util.List;

/**
 * 게시글 요청의 입구입니다. 서비스에 처리를 맡기고 Model에 JSP가 사용할 자료를 담습니다.
 */

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
	private final CommentService commentService;
	private final BoardService boardService;
	private final TycoonService tycoonService;
	private final ViewData viewData;
	private final LoginSessions loginSessions;

	private long userId(HttpSession session) {
		return loginSessions.require(session);
	}

	@GetMapping
	public String list() {
		return "redirect:/tycoon";
	}

	@GetMapping("/write")
	public String write(HttpSession session, HttpServletRequest request, Model model) {
		tycoonService.requireBoard(userId(session));
		viewData.prepare(request, model);
		return "board/write";
	}

	@PostMapping
	public String create(
			HttpSession session,
			@RequestParam String title,
			@RequestParam String content,
			@RequestParam(required = false) List<MultipartFile> files) throws IOException {
		return "redirect:/board/" + boardService.create(userId(session), title, content, files);
	}

	@GetMapping("/{boardId}")
	public String detail(HttpSession session, @PathVariable long boardId, Model model, HttpServletRequest request) {
		model.addAttribute("post", boardService.get(userId(session), boardId));
		if (tycoonService.get(userId(session)).getHasComments()) {
			model.addAttribute("comments", commentService.list(userId(session), boardId));
		}
		model.addAttribute("attachments", boardService.attachments(userId(session), boardId));
		viewData.prepare(request, model);
		return "board/detail";
	}

	@GetMapping("/{boardId}/edit")
	public String edit(HttpSession session, @PathVariable long boardId, Model model, HttpServletRequest request) {
		model.addAttribute("post", boardService.own(userId(session), boardId));
		model.addAttribute("attachments", boardService.attachments(userId(session), boardId));
		viewData.prepare(request, model);
		return "board/edit";
	}

	@PostMapping("/{boardId}/edit")
	public String edit(
			HttpSession session,
			@PathVariable long boardId,
			@RequestParam String title,
			@RequestParam String content,
			@RequestParam(required = false) List<MultipartFile> files,
			@RequestParam(required = false) List<Long> removeIds) throws IOException {
		boardService.edit(userId(session), boardId, title, content, files, removeIds);
		return "redirect:/board/" + boardId;
	}

	@PostMapping("/{boardId}/delete")
	public String delete(HttpSession session, @PathVariable long boardId) {
		boardService.delete(userId(session), boardId);
		return "redirect:/board";
	}
}
