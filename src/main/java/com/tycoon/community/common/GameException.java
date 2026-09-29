package com.tycoon.community.common;

/**
 * 잘못된 게임 요청을 중단할 때 던지는 예외입니다. 안내 문구와 HTTP 상태를 함께 전달합니다.
 */

public class GameException extends RuntimeException {
	private final int status;

	public GameException(String message) {
		this(400, message);
	}

	public GameException(int status, String message) {
		super(message);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
