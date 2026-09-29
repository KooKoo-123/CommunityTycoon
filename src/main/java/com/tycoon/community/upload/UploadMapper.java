package com.tycoon.community.upload;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * MyBatis SQL 호출 목록입니다. resources/mapper/UploadMapper.xml의 namespace는 이 인터페이스, id는 메서드 이름에 대응합니다. @Param 이름은 SQL의 #{이름}과 연결됩니다.
 */

@Mapper
public interface UploadMapper {

	int insert(UploadFile file);

	UploadFile find(long uploadId);

	List<UploadFile> attachments(long boardId);

	void link(@Param("boardId") long boardId, @Param("uploadId") long uploadId);

	int unlink(@Param("boardId") long boardId, @Param("uploadId") long uploadId);

	int enqueueDeletion(long uploadId);

	int deleteDetached(long uploadId);

	List<String> pendingDeletions();

	int completeDeletion(String objectKey);
}
