package com.tycoon.community.board;

import lombok.Data;

/**
 * 브라우저가 요청한 페이지·검색 조건을 담아 매퍼 SQL로 전달합니다.
 */

@Data
public class PageRequestDTO {
	private int page = 1;
	private int size = 10;
	private String title;
	private String writer;
	private long gameDataId;
	private boolean paging;

	public int getSkip() { return (page - 1) * size; }
}
