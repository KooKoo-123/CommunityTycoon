package com.tycoon.community.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import com.tycoon.community.user.User;
import com.tycoon.community.user.UserService;
import com.tycoon.community.tycoon.ModuleShop;
import com.tycoon.community.tycoon.GameData;
import com.tycoon.community.tycoon.TycoonService;
import lombok.RequiredArgsConstructor;

/**
 * 각 컨트롤러가 직접 호출하는 화면 준비 도구입니다. 회원·테마·게임·가격 정보를 Model에 넣습니다.
 */

@Component
@RequiredArgsConstructor
public class ViewData {

	private final UserService memberService;
	private final TycoonService tycoonService;

	public void prepare(HttpServletRequest request, Model model) {
		String theme = "sky";
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("tycoonTheme".equals(cookie.getName()) && "forest".equals(cookie.getValue())) {
					theme = "forest";
				}
			}
		}
		if (request.getSession(false) != null) {
			Long userId = (Long) request.getSession(false).getAttribute("userId");
			if (userId != null) {
				User member = memberService.get(userId);
				model.addAttribute("currentMember", member);
				GameData site = tycoonService.get(userId);
				model.addAttribute("site", site);
				theme = "forest".equals(site.getUserTheme()) ? "forest" : "sky";
			}
		}
		model.addAttribute("selectedTheme", theme);
		model.addAttribute("modulePrices", ModuleShop.prices());
		model.addAttribute("price", 150);
		model.addAttribute("commentPrice", 200);
	}
}
