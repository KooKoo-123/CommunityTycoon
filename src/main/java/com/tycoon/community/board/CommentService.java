package com.tycoon.community.board;

import java.util.List;

/**
 * 사용자별 댓글 조회와 작성자 권한이 필요한 변경 기능의 서비스 계약입니다.
 */

public interface CommentService {

	List<Comment> list(long userId, long boardId);

	long create(long userId, long boardId, String content);

	void edit(long userId, long boardId, long commentId, String content);

	void delete(long userId, long boardId, long commentId);
}
