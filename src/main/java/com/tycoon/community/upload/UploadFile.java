package com.tycoon.community.upload;

import lombok.Data;

/**
 * S3 파일의 메타데이터입니다. 실제 파일 바이트가 아니라 소유자·저장 키·이름·종류·크기를 담습니다.
 */

@Data
public class UploadFile {
	private Long uploadId;
	private Long userId;
	private String objectKey;
	private String originalName;
	private String contentType;
	private long fileSize;
	private String category;

	public boolean getImage() {
		return "image/png".equals(contentType) || "image/jpeg".equals(contentType) || "image/gif".equals(contentType);
	}
}
