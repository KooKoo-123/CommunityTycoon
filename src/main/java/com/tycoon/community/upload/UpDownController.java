package com.tycoon.community.upload;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.tycoon.community.board.BoardService;
import com.tycoon.community.common.GameException;

import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import com.tycoon.community.user.LoginSessions;

/**
 * 프로필 업로드·보관함 선택·파일 열기 요청을 받아 UploadService에 전달합니다.
 */

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class UpDownController {
	private final UploadService uploadService;
	private final LoginSessions loginSessions;

	private long userId(HttpSession session) {
		return loginSessions.require(session);
	}

	@PostMapping("/profile-image")
	public RedirectView profileImage(HttpSession session, @RequestParam MultipartFile file) throws IOException {
		uploadService.profileImage(userId(session), file);
		return new RedirectView("/member/mypage", true, false, false);
	}

	@PostMapping("/profile-image/select")
	public RedirectView selectProfileImage(HttpSession session, @RequestParam long profileImageStorageId) {
		uploadService.selectProfileImage(userId(session), profileImageStorageId);
		return new RedirectView("/member/mypage", true, false, false);
	}

	@PostMapping("/profile-image/delete")
	public RedirectView deleteProfileImage(HttpSession session, @RequestParam long profileImageStorageId) {
		uploadService.deleteProfileImageStorage(userId(session), profileImageStorageId);
		return new RedirectView("/member/mypage", true, false, false);
	}

	@GetMapping("/{uploadId}")
	public RedirectView download(HttpSession session, @PathVariable long uploadId,
			HttpServletResponse response, @RequestParam(defaultValue = "false") boolean download) throws IOException {
		response.setHeader("Cache-Control", "no-store");
		return new RedirectView(uploadService.download(userId(session), uploadId, !download), false, false, false);
	}
}
