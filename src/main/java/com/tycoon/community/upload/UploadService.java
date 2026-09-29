package com.tycoon.community.upload;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tycoon.community.common.GameException;
import com.tycoon.community.tycoon.TycoonService;
import com.tycoon.community.tycoon.TycoonMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import com.tycoon.community.user.ProfileImageStorage;
import com.tycoon.community.user.ProfileImageStorageMapper;

/**
 * 모듈과 파일 소유자를 확인하고 S3 업로드 결과를 DB 파일 기록 및 프로필 이력에 연결합니다.
 */

@Service
@RequiredArgsConstructor
public class UploadService {
	private final S3Uploader s3Uploader;
	private final UploadMapper uploadMapper;
	private final TycoonMapper tycoonMapper;
	private final TycoonService tycoonService;
	private final ProfileImageStorageMapper profileImageStorageMapper;

	public long upload(long userId, MultipartFile file, String category) throws IOException {
		tycoonService.requireModule(userId, category);
		UploadFile result = s3Uploader.upload(userId, file, category);
		uploadMapper.insert(result);
		return result.getUploadId();
	}

	@Transactional(rollbackFor = Exception.class)
	public void profileImage(long userId, MultipartFile file) throws IOException {
		long profileImageId = upload(userId, file, "profileImage");
		ProfileImageStorage profileImageStorage = new ProfileImageStorage();
		profileImageStorage.setUploadId(profileImageId);
		profileImageStorageMapper.insert(profileImageStorage);
		tycoonMapper.updateProfileImage(userId, profileImageStorage.getProfileImageStorageId());
	}

	public void selectProfileImage(long userId, long profileImageStorageId) {
		tycoonService.requireModule(userId, "profileImageStorage");
		ProfileImageStorage profileImageStorage = profileImageStorageMapper.find(profileImageStorageId, userId);
		if (profileImageStorage == null) throw new GameException(404, "내 보관함의 사진을 선택해 주세요.");
		tycoonMapper.updateProfileImage(userId, profileImageStorage.getProfileImageStorageId());
	}

	public void deleteProfileImageStorage(long userId, long profileImageStorageId) {
		tycoonService.requireModule(userId, "profileImageStorage");
		if (Long.valueOf(profileImageStorageId).equals(tycoonService.get(userId).getUserProfileImageId())) {
			throw new GameException(400, "현재 사용 중인 사진은 먼저 다른 사진으로 변경해 주세요.");
		}
		if (profileImageStorageMapper.delete(profileImageStorageId, userId) == 0) {
			throw new GameException(404, "내 보관함의 사진을 선택해 주세요.");
		}
	}

	public String download(long userId, long uploadId, boolean inline) throws IOException {
		UploadFile file = uploadMapper.find(uploadId);
		if (file == null || file.getUserId() != userId) {
			throw new GameException(404, "파일을 찾을 수 없습니다.");
		}
		return s3Uploader.downloadUrl(file, inline);
	}
}
