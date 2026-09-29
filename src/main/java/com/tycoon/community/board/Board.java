package com.tycoon.community.board;

import lombok.Data;

/**
 * 게시글 한 건을 담는 객체입니다. DB id는 링크용이고 displayNo는 내 사이트에서 보이는 순번입니다.
 */

@Data
public class Board {
	private Long boardId;
	private int displayNo;
	private Long gameDataId;
	private String authorType;
	private String authorNickname;
	private Long authorProfileImageId;
	private String firstImageUrl;
	private String title;
	private String content;
	private int createdDay;
	private java.sql.Timestamp createdAt;
}
