package com.tycoon.community.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis SQL 호출 목록입니다. resources/mapper/CommentMapper.xml의 namespace는 이 인터페이스, id는 메서드 이름에 대응합니다. @Param 이름은 SQL의 #{이름}과 연결됩니다.
 */

@Mapper
public interface CommentMapper {

	List<Comment> list(long boardId);

	Comment find(@Param("commentId") long commentId, @Param("boardId") long boardId);

	int insert(Comment comment);

	int update(@Param("commentId") long commentId, @Param("boardId") long boardId,
			@Param("content") String content);

	int delete(@Param("commentId") long commentId, @Param("boardId") long boardId);
}
