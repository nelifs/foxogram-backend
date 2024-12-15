package su.foxogram.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.repositories.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class StorageService {

	private static final String ALGORITHM = "MD5";

	private static final String CONTENT_TYPE_WEBP = "image/webp";

	private static final String CONTENT_TYPE_PNG = "image/png";

	private final MinioClient minioClient;

	private final UserRepository userRepository;

	@Autowired
	public StorageService(UserRepository userRepository, MinioClient minioClient) {
		this.minioClient = minioClient;
		this.userRepository = userRepository;
	}

	public String uploadFile(MultipartFile file, String bucketName) throws RuntimeException, IOException {

		byte[] byteArray = file.getBytes();
		log.info("Uploading file ({}) to bucket ({})", file.getOriginalFilename(), bucketName);

		if (!isBucketExist(bucketName)) createBucket(bucketName);

		try {
			String fileHash = getHash(byteArray);
			InputStream inputStream = file.getInputStream();

			//if (isHashExists(fileHash)) return fileHash;

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileHash + ".png").stream(
									inputStream, inputStream.available(), -1)
							.contentType(CONTENT_TYPE_PNG)
							.build());

			log.info("Image ({}) in PNG uploaded to bucket ({}) to CDN successfully", fileHash, bucketName);

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileHash + ".webp").stream(
									inputStream, inputStream.available(), -1)
							.contentType(CONTENT_TYPE_WEBP)
							.build());

			log.info("Image ({}) in WEBP uploaded to bucket ({}) to CDN successfully", fileHash, bucketName);
			return fileHash;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private boolean isHashExists(String hash) {
		return userRepository.findByAvatar(hash) == null;
	}

	private void createBucket(String bucketName) {
		try {
			minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private boolean isBucketExist(String bucketName) throws RuntimeException {
		try {
			return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
		} catch (Exception e) {
			throw new RuntimeException();
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
