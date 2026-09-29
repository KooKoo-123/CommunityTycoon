package com.tycoon.community.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import lombok.RequiredArgsConstructor;

/**
 * DB에 저장한 첨부 삭제 대기열을 처리합니다. DB 변경이 확정된 뒤 S3 파일을 지우고 실패하면 재시도합니다.
 */

@Service
@EnableScheduling
@RequiredArgsConstructor
public class S3DeletionService {
    private static final Logger log = LoggerFactory.getLogger(S3DeletionService.class);
    private final UploadMapper uploadMapper;
    private final S3Uploader s3Uploader;
    private final PlatformTransactionManager transactionManager;

    public void deleteDetached(long uploadId) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("첨부파일 삭제에는 트랜잭션이 필요합니다.");
        }
        if (uploadMapper.enqueueDeletion(uploadId) == 0) return;
        uploadMapper.deleteDetached(uploadId);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { retryPending(); }
        });
    }

    // 예약된 간격으로 삭제 대기열을 재처리해 S3 장애나 서버 재시작 후에도 작업을 이어갑니다.

    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public synchronized void retryPending() {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        try {
            transaction.execute(status -> {
                for (String key : uploadMapper.pendingDeletions()) {
                    try {
                        s3Uploader.delete(key);
                        uploadMapper.completeDeletion(key);
                    } catch (RuntimeException exception) {
                        log.warn("S3 삭제를 다음 주기에 재시도합니다: {}", key, exception);
                    }
                }
                return null;
            });
        } catch (RuntimeException exception) {
            log.warn("첨부파일 삭제 대기열을 처리하지 못했습니다. 다음 주기에 재시도합니다.", exception);
        }
    }
}
