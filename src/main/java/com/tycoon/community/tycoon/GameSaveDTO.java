package com.tycoon.community.tycoon;

import lombok.Data;

/**
 * JS가 보낸 게임 상태와 NPC 글 자료를 받는 전달 객체입니다. 서비스가 검사한 뒤 DB에 반영합니다.
 */

@Data
public class GameSaveDTO {
	private long gold;
	private int traffic;
	private int day;
	private int playSeconds;
	private boolean board;
	private boolean comment;
	private boolean mypage;
	private boolean profileImage;
	private boolean attachment;
	private boolean paging;
	private boolean search;
	private boolean theme;
	private boolean profileImageStorage;
	private String title;
	private String content;
	private String author;
	private String reply;
	private String replyAuthor;
	private String replyBack;
}
