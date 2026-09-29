package com.tycoon.community.tycoon;

/**
 * 사용자별 게임 상태, 기능 해금과 게임 행동을 처리하는 서비스 계약입니다.
 */

public interface TycoonService {
	public static final long BOARD_PRICE = 150;
	public static final long COMMENT_PRICE = 200;

	GameData requireComments(long userId);

	GameData get(long userId);

	GameData requireBoard(long userId);

	long save(long userId, GameSaveDTO data);

	long npc(long userId, GameSaveDTO data);

	GameData action(long userId, GameSaveDTO data, String action, String module);

	void requireModule(long userId, String module);
}
