package com.tycoon.community.user;

import java.util.Date;
import lombok.Data;

/**
 * 과거 프로필 사진 한 건입니다. 이력 번호와 업로드 번호는 서로 다른 테이블의 번호입니다.
 */

@Data
public class ProfileImageStorage {

	private Long profileImageStorageId;
	private Long uploadId;
	private Date createdAt;
}
