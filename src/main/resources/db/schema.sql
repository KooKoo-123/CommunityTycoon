-- Community Tycoon의 신규 데이터베이스를 만드는 단일 기준 스키마입니다.
-- MariaDB / InnoDB 기준이며, 기존 DB에 적용하는 변경 스크립트가 아닙니다.
-- 테이블 생성 순서는 외래키의 부모 테이블을 먼저 만들도록 정했습니다.

-- 로그인 계정 정보를 저장합니다.
CREATE TABLE IF NOT EXISTS ct_user (
    userId BIGINT NOT NULL AUTO_INCREMENT,
    loginId VARCHAR(30) NOT NULL,
    password VARCHAR(60) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    PRIMARY KEY (userId),
    UNIQUE KEY uq_ct_user_loginId (loginId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 사용자 한 명의 게임 진행 상태와 구매한 기능을 저장합니다.
-- userId의 UNIQUE 제약으로 한 계정당 게임 데이터가 하나만 생기게 합니다.
CREATE TABLE IF NOT EXISTS ct_game_data (
    gameDataId BIGINT NOT NULL AUTO_INCREMENT,
    userId BIGINT NOT NULL,
    `day` INT NOT NULL DEFAULT 1,
    gold BIGINT NOT NULL DEFAULT 200,
    traffic INT NOT NULL DEFAULT 0,
    playSeconds INT NOT NULL DEFAULT 0,
    hasBoard BOOLEAN NOT NULL DEFAULT FALSE,
    hasComments BOOLEAN NOT NULL DEFAULT FALSE,
    hasMypage BOOLEAN NOT NULL DEFAULT FALSE,
    hasProfileImage BOOLEAN NOT NULL DEFAULT FALSE,
    hasAttachment BOOLEAN NOT NULL DEFAULT FALSE,
    hasPaging BOOLEAN NOT NULL DEFAULT FALSE,
    hasSearch BOOLEAN NOT NULL DEFAULT FALSE,
    hasTheme BOOLEAN NOT NULL DEFAULT FALSE,
    hasProfileImageStorage BOOLEAN NOT NULL DEFAULT FALSE,
    userTheme VARCHAR(20) NOT NULL DEFAULT 'sky',
    userProfileImageId BIGINT NULL,
    userNickname VARCHAR(30) NOT NULL,
    PRIMARY KEY (gameDataId),
    UNIQUE KEY uq_ct_game_data_userId (userId),
    CONSTRAINT fk_ct_game_data_user
        FOREIGN KEY (userId) REFERENCES ct_user(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- S3에 올린 파일 자체가 아니라, 파일을 찾고 보여 주는 정보를 저장합니다.
CREATE TABLE IF NOT EXISTS ct_upload (
    uploadId BIGINT NOT NULL AUTO_INCREMENT,
    userId BIGINT NOT NULL,
    objectKey VARCHAR(300) NOT NULL,
    originalName VARCHAR(255) NOT NULL,
    contentType VARCHAR(100) NOT NULL,
    fileSize BIGINT NOT NULL,
    category VARCHAR(20) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (uploadId),
    UNIQUE KEY uq_ct_upload_objectKey (objectKey),
    CONSTRAINT fk_ct_upload_user
        FOREIGN KEY (userId) REFERENCES ct_user(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 게시글과 작성 당시 표시 정보를 저장합니다.
CREATE TABLE IF NOT EXISTS ct_board (
    boardId BIGINT NOT NULL AUTO_INCREMENT,
    gameDataId BIGINT NOT NULL,
    authorType VARCHAR(10) NOT NULL,
    authorNickname VARCHAR(30) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    createdDay INT NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    authorProfileImageId BIGINT NULL,
    PRIMARY KEY (boardId),
    KEY idx_ct_board_gameData_author_board (gameDataId, authorType, boardId),
    CONSTRAINT fk_ct_board_game_data
        FOREIGN KEY (gameDataId) REFERENCES ct_game_data(gameDataId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 댓글은 게시글에 속합니다. authorType으로 PLAYER와 NPC를 구분합니다.
CREATE TABLE IF NOT EXISTS ct_comment (
    commentId BIGINT NOT NULL AUTO_INCREMENT,
    boardId BIGINT NOT NULL,
    authorType VARCHAR(10) NOT NULL,
    authorNickname VARCHAR(30) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    authorProfileImageId BIGINT NULL,
    PRIMARY KEY (commentId),
    CONSTRAINT fk_ct_comment_board
        FOREIGN KEY (boardId) REFERENCES ct_board(boardId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 게시글 여러 개와 업로드 여러 개의 연결을 행 단위로 저장합니다.
-- 복합 기본키가 같은 파일이 같은 글에 중복 연결되는 것을 막습니다.
CREATE TABLE IF NOT EXISTS ct_board_attachment (
    boardId BIGINT NOT NULL,
    uploadId BIGINT NOT NULL,
    PRIMARY KEY (boardId, uploadId),
    CONSTRAINT fk_ct_board_attachment_board
        FOREIGN KEY (boardId) REFERENCES ct_board(boardId) ON DELETE CASCADE,
    CONSTRAINT fk_ct_board_attachment_upload
        FOREIGN KEY (uploadId) REFERENCES ct_upload(uploadId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 예전 프로필 사진 보관함입니다. 실제 파일 정보는 uploadId로 ct_upload에서 찾습니다.
CREATE TABLE IF NOT EXISTS ct_profile_image_storage (
    profileImageStorageId BIGINT NOT NULL AUTO_INCREMENT,
    uploadId BIGINT NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (profileImageStorageId),
    UNIQUE KEY uq_ct_profile_image_storage_uploadId (uploadId),
    CONSTRAINT fk_ct_profile_image_storage_upload
        FOREIGN KEY (uploadId) REFERENCES ct_upload(uploadId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- DB에서 연결을 끊은 뒤 S3 삭제가 실패하면 재시도 키를 보관합니다.
-- 업로드 기록과 연결하지 않아 원본 행이 정리되어도 재시도할 수 있습니다.
CREATE TABLE IF NOT EXISTS ct_s3_deletion (
    objectKey VARCHAR(300) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (objectKey)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
