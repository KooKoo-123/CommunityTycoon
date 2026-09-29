package com.tycoon.community.board;

import java.util.List;
import lombok.Getter;

/**
 * 조회한 글 목록과 전체 개수로 현재 페이지 및 이전/다음 페이지 버튼 정보를 만듭니다.
 */

@Getter
public class PageResponseDTO {
	private int page;
	private int total;
	private int start;
	private int end;
	private boolean prev;
	private boolean next;
	private List<Board> dtoList;

	public PageResponseDTO(PageRequestDTO request, List<Board> posts, int total) {
		this.page = request.getPage();
		this.total = total;
		this.dtoList = posts;
		int last = Math.max(1, (int) Math.ceil(total / (double) request.getSize()));
		this.start = ((page - 1) / 10) * 10 + 1;
		this.end = Math.min(start + 9, last);
		this.prev = start > 1;
		this.next = end < last;
	}
}
