package com.tycoon.community.tycoon;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 모듈 이름과 골드 가격을 한곳에 모은 가격표입니다. 서버 계산과 JSP 가격 표시가 함께 사용합니다.
 */

public class ModuleShop {

	public static Map<String, Integer> prices() {
		Map<String, Integer> prices = new LinkedHashMap<>();
		prices.put("board", 150);
		prices.put("comment", 200);
		prices.put("mypage", 100);
		prices.put("profileImage", 150);
		prices.put("attachment", 250);
		prices.put("paging", 150);
		prices.put("search", 150);
		prices.put("theme", 100);
		prices.put("profileImageStorage", 250);
		return prices;
	}
}
