package com.tycoon.community.board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tycoon.community.common.GameException;
import com.tycoon.community.tycoon.GameData;
import com.tycoon.community.tycoon.TycoonService;

import lombok.RequiredArgsConstructor;

/**
 * 댓글 모듈과 작성자 권한을 확인한 다음 댓글을 조회하거나 변경합니다.
 */

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentMapper commentMapper;
	private final BoardService boardService;
	private final TycoonService tycoonService;

	private void requireAccess(long userId, long boardId) {
		tycoonService.requireComments(userId);
		// 주소를 직접 바꿔도 다른 운영자의 게시글에 접근할 수 없습니다.
		boardService.get(userId, boardId);
	}

	@Override
	public List<Comment> list(long userId, long boardId) {
		requireAccess(userId, boardId);
		return commentMapper.list(boardId);
	}

	private String validate(String content) {
		if (content == null || content.trim().isEmpty() || content.length() > 1000) {
			throw new GameException("댓글은 1~1,000자로 입력해 주세요.");
		}
		return content.trim();
	}

	private Comment own(long userId, long boardId, long commentId) {
		requireAccess(userId, boardId);
		Comment comment = commentMapper.find(commentId, boardId);
		if (comment == null) {
			throw new GameException(404, "댓글을 찾을 수 없습니다.");
		}
		if (!"PLAYER".equals(comment.getAuthorType())) {
			throw new GameException(403, "직접 작성한 댓글만 수정하거나 삭제할 수 있어요.");
		}
		return comment;
	}

	@Transactional
	@Override
	public long create(long userId, long boardId, String content) {
		requireAccess(userId, boardId);
		Comment comment = new Comment();
		comment.setBoardId(boardId);
		comment.setAuthorType("PLAYER");
		GameData site = tycoonService.get(userId);
		comment.setAuthorNickname(site.getUserNickname());
		comment.setAuthorProfileImageId(site.getUserProfileImageUploadId());
		comment.setContent(validate(content));
		commentMapper.insert(comment);
		return comment.getCommentId();
	}

	@Transactional
	@Override
	public void edit(long userId, long boardId, long commentId, String content) {
		Comment comment = own(userId, boardId, commentId);
		comment.setContent(validate(content));
		commentMapper.update(comment.getCommentId(), comment.getBoardId(), comment.getContent());
	}

	@Transactional
	@Override
	public void delete(long userId, long boardId, long commentId) {
		own(userId, boardId, commentId);
		commentMapper.delete(commentId, boardId);
	}
}
