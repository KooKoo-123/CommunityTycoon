package com.tycoon.community.board;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.tycoon.community.upload.UploadFile;

/**
 * 사용자별 게시글 목록·상세·변경과 첨부 조회를 제공하는 서비스 계약입니다.
 */

public interface BoardService {

	PageResponseDTO page(long userId, PageRequestDTO request);

	List<UploadFile> attachments(long userId, long boardId);

	List<Board> list(long userId);

	Board get(long userId, long boardId);

	Board own(long userId, long boardId);

	long create(long userId, String title, String content, List<MultipartFile> files) throws IOException;

	void edit(long userId, long boardId, String title, String content, List<MultipartFile> files, List<Long> removeIds) throws IOException;

	void delete(long userId, long boardId);
}
