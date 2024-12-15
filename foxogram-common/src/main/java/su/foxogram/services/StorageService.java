package su.foxogram.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioAsyncClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class StorageService {

	private static final String ALGORITHM = "MD5";

	private static final String CONTENT_TYPE_WEBP = "image/webp";

	private static final String CONTENT_TYPE_PNG = "image/png";

	private final MinioAsyncClient minioClient;

	@Autowired
	public StorageService(MinioAsyncClient minioClient) {
		this.minioClient = minioClient;
	}

	public String uploadFile(MultipartFile file, String bucketName) throws RuntimeException, IOException, ExecutionException, InterruptedException {
		byte[] byteArray = file.getBytes();
		long startTime = System.currentTimeMillis();
		log.info("Uploading file ({}) to bucket ({})", file.getOriginalFilename(), bucketName);

		if (!isBucketExist(bucketName).get()) createBucket(bucketName);

		try (InputStream pngStream = new ByteArrayInputStream(byteArray);
			 InputStream webpStream = new ByteArrayInputStream(byteArray)) {
			String fileHash = getHash(byteArray);

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileHash + ".png").stream(
									pngStream, pngStream.available(), -1)
							.contentType(CONTENT_TYPE_PNG)
							.build());

			log.info("Image ({}) in PNG uploaded to bucket ({}) to CDN successfully", fileHash, bucketName);

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileHash + ".webp").stream(
									webpStream, webpStream.available(), -1)
							.contentType(CONTENT_TYPE_WEBP)
							.build());

			long endTime = System.currentTimeMillis();
			log.info("Image ({}) in WEBP uploaded to bucket ({}) to CDN successfully in {} ms ({}, {})", fileHash, bucketName, endTime - startTime, startTime, endTime);
			return fileHash;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createBucket(String bucketName) {
		try {
			minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private CompletableFuture<Boolean> isBucketExist(String bucketName) throws RuntimeException {
		try {
			return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getHash(byte[] imageBytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
		byte[] hashBytes = messageDigest.digest(imageBytes);
		StringBuilder hexString = new StringBuilder();

		for (byte b : hashBytes) {
			hexString.append(String.format("%02x", b));
		}

		return hexString.toString();
	}
}