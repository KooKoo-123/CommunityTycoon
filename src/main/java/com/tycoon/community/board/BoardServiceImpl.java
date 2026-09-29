package com.tycoon.community.board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tycoon.community.common.GameException;
import com.tycoon.community.tycoon.GameData;
import com.tycoon.community.tycoon.TycoonService;

import lombok.RequiredArgsConstructor;
import com.tycoon.community.upload.UploadService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.tycoon.community.upload.UploadFile;
import com.tycoon.community.upload.UploadMapper;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;

/**
 * 게시판 구매 여부와 글 소유자를 확인하고 게시글·첨부파일의 저장 순서를 관리합니다.
 */

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
	private final BoardMapper boardMapper;
	private final TycoonService tycoonService;
	private final UploadService uploadService;
	private final UploadMapper uploadMapper;
	private final com.tycoon.community.upload.S3DeletionService s3DeletionService;

	@Override
	public List<Board> list(long userId) {
		List<Board> posts = boardMapper.list(tycoonService.requireBoard(userId).getGameDataId());
		int number = posts.size();
		for (Board post : posts) {
			post.setDisplayNo(number);
			number--;
		}
		return posts;
	}

	@Override
	public Board get(long userId, long boardId) {
		Board post = boardMapper.find(boardId, tycoonService.requireBoard(userId).getGameDataId());
		if (post == null) {
			throw new GameException(404, "게시글을 찾을 수 없습니다.");
		}
		return post;
	}

	@Override
	public Board own(long userId, long boardId) {
		Board post = get(userId, boardId);
		if (!"PLAYER".equals(post.getAuthorType())) {
			throw new GameException(403, "직접 작성한 글만 수정하거나 삭제할 수 있어요.");
		}
		return post;
	}

	private void validate(String title, String content) {
		if (title == null || title.trim().isEmpty() || title.trim().length() > 100) {
			throw new GameException("제목은 1~100자로 입력해 주세요.");
		}
		if (content == null || content.trim().isEmpty() || content.length() > 10000) {
			throw new GameException("본문은 1~10,000자로 입력해 주세요.");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public long create(long userId, String title, String content,
			List<MultipartFile> files) throws IOException {
		GameData site = tycoonService.requireBoard(userId);
		validate(title, content);
		Board post = new Board();
		post.setGameDataId(site.getGameDataId());
		post.setAuthorType("PLAYER");
		post.setAuthorNickname(site.getUserNickname());
		post.setAuthorProfileImageId(site.getUserProfileImageUploadId());
		post.setTitle(title.trim());
		post.setContent(content.trim());
		post.setCreatedDay(site.getDay());

		boardMapper.insert(post);
		updateFiles(userId, post.getBoardId(), files, Collections.emptyList());
		return post.getBoardId();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void edit(long userId, long boardId, String title, String content, List<MultipartFile> files, List<Long> removeIds) throws IOException {
		Board post = own(userId, boardId);
		validate(title, content);
		post.setTitle(title.trim());
		post.setContent(content.trim());
		boardMapper.update(post);
		updateFiles(userId, boardId, files, removeIds);
	}

	@Transactional
	@Override
	public void delete(long userId, long boardId) {
		Board post = own(userId, boardId);
		List<UploadFile> files = uploadMapper.attachments(boardId);
		boardMapper.delete(boardId, post.getGameDataId());
		for (UploadFile file : files) s3DeletionService.deleteDetached(file.getUploadId());
	}

	@Override
	public PageResponseDTO page(long userId, PageRequestDTO request) {
		GameData site = tycoonService.requireBoard(userId);
		if (!site.getHasSearch() && ((request.getTitle() != null && !request.getTitle().isEmpty())
				|| (request.getWriter() != null && !request.getWriter().isEmpty()))) {
			throw new GameException(403, "검색 모듈을 먼저 구매해 주세요.");
		}
		if (!site.getHasPaging() && request.getPage() > 1) throw new GameException(403, "페이징 모듈을 먼저 구매해 주세요.");
		request.setGameDataId(site.getGameDataId());
		request.setPaging(site.getHasPaging());
		request.setSize(10);
		int total = boardMapper.getCount(request);
		int last = Math.max(1, (int) Math.ceil(total / 10.0));
		request.setPage(site.getHasPaging() ? Math.max(1, Math.min(last, request.getPage())) : 1);
		return new PageResponseDTO(request, boardMapper.selectPage(request), total);
	}

	@Override
	public List<UploadFile> attachments(long userId, long boardId) {
		get(userId, boardId);
		return uploadMapper.attachments(boardId);
	}

	private void updateFiles(long userId, long boardId, List<MultipartFile> files, List<Long> removeIds) throws IOException {
		List<MultipartFile> added = new ArrayList<>();
		if (files != null) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) added.add(file);
			}
		}
		List<Long> removed = removeIds == null ? Collections.emptyList() : removeIds;
		if (added.isEmpty() && removed.isEmpty()) return;
		tycoonService.requireModule(userId, "attachment");
		List<UploadFile> current = uploadMapper.attachments(boardId);
		Set<Long> currentIds = new HashSet<>();
		for (UploadFile file : current) currentIds.add(file.getUploadId());
		Set<Long> uniqueRemoved = new HashSet<>(removed);
		if (!currentIds.containsAll(uniqueRemoved)) throw new GameException("이 글의 첨부파일만 제거할 수 있습니다.");
		if (current.size() - uniqueRemoved.size() + added.size() > 5) throw new GameException("첨부파일은 최대 5개입니다.");
		for (MultipartFile file : added) {
			if (file.getSize() > 10 * 1024 * 1024) throw new GameException("각 파일은 10 MB 이하로 선택해 주세요.");
		}
		for (Long uploadId : uniqueRemoved) {
			uploadMapper.unlink(boardId, uploadId);
			s3DeletionService.deleteDetached(uploadId);
		}
		for (MultipartFile file : added) {
			long uploadId = uploadService.upload(userId, file, "attachment");
			uploadMapper.link(boardId, uploadId);
		}
	}
}
