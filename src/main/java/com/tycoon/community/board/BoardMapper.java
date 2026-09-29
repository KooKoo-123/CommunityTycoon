package com.tycoon.community.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis SQL 호출 목록입니다. resources/mapper/BoardMapper.xml의 namespace는 이 인터페이스, id는 메서드 이름에 대응합니다. @Param 이름은 SQL의 #{이름}과 연결됩니다.
 */

@Mapper
public interface BoardMapper {

	int deleteOldNpcComments(long gameDataId);

	int deleteOldNpcPosts(long gameDataId);

	List<Board> selectPage(PageRequestDTO request);

	int getCount(PageRequestDTO request);

	List<Board> list(long gameDataId);

	Board find(@Param("boardId") long boardId, @Param("gameDataId") long gameDataId);

	int insert(Board post);

	int update(Board post);

	int delete(@Param("boardId") long boardId, @Param("gameDataId") long gameDataId);
}
