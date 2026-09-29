package com.tycoon.community.tycoon;

import lombok.Data;

/**
 * 운영자 한 명의 게임 상태입니다. 골드·이용자·DAY와 has로 시작하는 모듈 구매 여부를 보관합니다.
 */

@Data
public class GameData {
	private Long gameDataId;
	private Long userId;
	private int day;
	private long gold;
	private int traffic;
	private int playSeconds;
	private boolean hasBoard;
	private boolean hasComments;
	private boolean hasMypage;
	private boolean hasProfileImage;
	private boolean hasAttachment;
	private boolean hasPaging;
	private boolean hasSearch;
	private boolean hasTheme;
	private boolean hasProfileImageStorage;
	private String userTheme;
	private Long userProfileImageId;
	private String userNickname;
	private Long userProfileImageUploadId;
	public boolean getHasProfileImageStorage() {
		return hasProfileImageStorage;
	}
	public boolean getHasMypage() {
		return hasMypage;
	}
	public boolean getHasProfileImage() {
		return hasProfileImage;
	}
	public boolean getHasAttachment() {
		return hasAttachment;
	}
	public boolean getHasPaging() {
		return hasPaging;
	}
	public boolean getHasSearch() {
		return hasSearch;
	}
	public boolean getHasTheme() {
		return hasTheme;
	}
	public boolean getHasBoard() {
		return hasBoard;
	}
	public boolean getHasComments() {
		return hasComments;
	}
}
