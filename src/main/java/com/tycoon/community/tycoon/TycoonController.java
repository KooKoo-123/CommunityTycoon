package com.tycoon.community.tycoon;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.tycoon.community.board.BoardService;
import com.tycoon.community.board.PageRequestDTO;
import com.tycoon.community.board.PageResponseDTO;
import com.tycoon.community.common.ViewData;
import com.tycoon.community.user.LoginSessions;
import lombok.RequiredArgsConstructor;

/**
 * 게임 화면과 fetch 요청의 입구입니다. @ResponseBody 메서드는 JSP 대신 데이터나 문자열을 반환합니다.
 */

@Controller
@RequestMapping("/tycoon")
@RequiredArgsConstructor
public class TycoonController {

	private final TycoonService tycoonService;
	private final BoardService boardService;
	private final ViewData viewData;
	private final LoginSessions loginSessions;

	@GetMapping
	public String main(HttpSession session, HttpServletRequest httpRequest, Model model, PageRequestDTO request) {
		if (session.getAttribute("userId") == null) return "redirect:/member/login";
		long userId = loginSessions.require(session);
		viewData.prepare(httpRequest, model);
		model.addAttribute("serverTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		if (tycoonService.get(userId).getHasBoard()) {
			PageResponseDTO result = boardService.page(userId, request);
			model.addAttribute("posts", result.getDtoList());
			model.addAttribute("pagination", result);
			model.addAttribute("pageRequest", request);
		}
		return "tycoon/main";
	}

	@PostMapping(value = {"/save", "/progress"}, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String save(HttpSession session, GameSaveDTO data) {
		synchronized (loginSessions) {
			return String.valueOf(tycoonService.save(loginSessions.require(session), data));
		}
	}

	@PostMapping(value = "/npc", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String npc(HttpSession session, GameSaveDTO data) {
		synchronized (loginSessions) {
			long userId = loginSessions.require(session);
			long boardId = tycoonService.npc(userId, data);
			return boardId + "," + boardService.get(userId, boardId).getDisplayNo();
		}
	}

	@GetMapping("/posts")
	@ResponseBody
	public PageResponseDTO posts(HttpSession session, PageRequestDTO request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store");
		return boardService.page(loginSessions.require(session), request);
	}

	@GetMapping("/state")
	@ResponseBody
	public GameData state(HttpSession session, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store");
		return tycoonService.get(loginSessions.require(session));
	}

	@PostMapping("/action")
	@ResponseBody
	public GameData action(HttpSession session, GameSaveDTO data,
			@RequestParam String action, @RequestParam(defaultValue = "") String module) {
		synchronized (loginSessions) {
			return tycoonService.action(loginSessions.require(session), data, action, module);
		}
	}
}
