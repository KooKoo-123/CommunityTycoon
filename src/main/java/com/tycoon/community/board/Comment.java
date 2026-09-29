package com.tycoon.community.board;

import lombok.Data;

/**
 * 댓글 한 건을 담습니다. 작성자 유형과 작성 당시 표시 정보를 보관합니다.
 */

@Data
public class Comment {
	private Long commentId;
	private Long boardId;
	private String authorType;
	private String authorNickname;
	private Long authorProfileImageId;
	private String displayName;
	private String content;
}
