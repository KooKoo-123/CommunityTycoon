package com.tycoon.community.upload;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.tycoon.community.common.GameException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;
import java.util.Iterator;
import java.util.Locale;
import com.amazonaws.AmazonClientException;

/**
 * AWS SDK로 실제 파일을 전송·삭제하고 제한 시간 동안 열 수 있는 서명 URL을 만듭니다.
 */

@Component
public class S3Uploader {
	@Value("${cloud.aws.s3.bucket:community-tycoon}")
	private String bucket;
	@Value("${cloud.aws.region.static:ap-northeast-2}")
	private String region;
	@Value("${cloud.aws.credentials.access-key:}")
	private String accessKey;
	@Value("${cloud.aws.credentials.secret-key:}")
	private String secretKey;

	private AmazonS3 client() {
		AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().withRegion(region);
		if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
			builder.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));
		}
		// 별도 키가 없으면 AWS_ACCESS_KEY_ID 등의 표준 환경변수를 사용합니다.
		return builder.build();
	}

	public UploadFile upload(long userId, MultipartFile file, String category) throws IOException {
		if (file == null || file.isEmpty()) throw new GameException("파일을 선택해 주세요.");
		long max = "profileImage".equals(category) ? 5 * 1024 * 1024 : 10 * 1024 * 1024;
		if (file.getSize() > max) throw new GameException("프로필은 5 MB, 첨부파일은 10 MB까지 가능합니다.");
		String name = file.getOriginalFilename();
		if (name == null) name = "file";
		name = name.replace('\\', '/');
		name = name.substring(name.lastIndexOf('/') + 1);
		if (name.length() > 255) throw new GameException("파일명이 너무 깁니다.");
		String contentType = "application/octet-stream";
		boolean imageName = name.toLowerCase(Locale.ROOT).matches(".*\\.(png|jpg|jpeg|gif)$");
		if ("profileImage".equals(category) || imageName) {
			// HTML/SVG를 사진으로 받지 않습니다. 실제 이미지인지 확인합니다.
			try (InputStream input = file.getInputStream();
					ImageInputStream image = ImageIO.createImageInputStream(input)) {
				if (image == null) throw new GameException("사진을 읽지 못했습니다.");
				Iterator<ImageReader> readers = ImageIO.getImageReaders(image);
				if (!readers.hasNext()) throw new GameException("PNG, JPG, GIF 사진을 선택해 주세요.");
				ImageReader reader = readers.next();
				try {
					reader.setInput(image);
					String format = reader.getFormatName().toLowerCase(Locale.ROOT);
					if (!format.matches("png|jpeg|jpg|gif") || reader.getWidth(0) > 4096 || reader.getHeight(0) > 4096) {
						throw new GameException("4096px 이하 PNG, JPG, GIF 사진을 선택해 주세요.");
					}
					contentType = "image/" + ("jpg".equals(format) ? "jpeg" : format);
				} finally { reader.dispose(); }
			}
		}
		UploadFile uploaded = new UploadFile();
		uploaded.setUserId(userId);
		uploaded.setCategory(category);
		uploaded.setOriginalName(name);
		uploaded.setContentType(contentType);
		uploaded.setFileSize(file.getSize());
		uploaded.setObjectKey(category + "/" + userId + "/" + UUID.randomUUID());
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		metadata.setContentType(contentType);
		AmazonS3 s3 = client();
		try (InputStream input = file.getInputStream()) {
			// PublicRead ACL을 강제로 설정하지 않아 비공개 버킷에서도 사용할 수 있습니다.
			s3.putObject(new PutObjectRequest(bucket, uploaded.getObjectKey(), input, metadata));
		} catch (AmazonClientException exception) {
			throw new GameException("S3에 업로드하지 못했어요. 버킷과 AWS 업로드 권한을 확인해 주세요.");
		} finally { s3.shutdown(); }
		return uploaded;
	}

	public void delete(String objectKey) {
		AmazonS3 s3 = client();
		try { s3.deleteObject(bucket, objectKey); }
		finally { s3.shutdown(); }
	}

	public String objectUrl(UploadFile file) {
		return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + file.getObjectKey();
	}

	public String downloadUrl(UploadFile file, boolean inline) throws IOException {
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, file.getObjectKey());
		request.setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
		ResponseHeaderOverrides headers = new ResponseHeaderOverrides();
		headers.setContentType(file.getContentType());
		if (!inline || !file.getImage()) {
			headers.setContentDisposition("attachment; filename*=UTF-8''" + URLEncoder.encode(file.getOriginalName(), "UTF-8").replace("+", "%20"));
		}
		request.setResponseHeaders(headers);
		AmazonS3 s3 = client();
		try { return s3.generatePresignedUrl(request).toString(); }
		finally { s3.shutdown(); }
	}
}
