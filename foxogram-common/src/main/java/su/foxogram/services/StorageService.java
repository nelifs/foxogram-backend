package su.foxogram.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.repositories.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class StorageService {

	private static final String ALGORITHM = "MD5";

	private static final String CONTENT_TYPE_WEBP = "image/webp";

	private static final String CONTENT_TYPE_PNG = "image/webp";

	@Autowired
	private MinioClient minioClient;

	@Autowired
	private UserRepository userRepository;

	public String uploadFile(String base64String, String bucketName) throws RuntimeException {
		byte[] byteArray = Base64.getDecoder().decode(base64String);

		if (!isBucketExist(bucketName)) createBucket(bucketName);

		try {
			String fileHash = getHash(byteArray);
			InputStream inputStream = new ByteArrayInputStream(byteArray);

			if (isHashExists(fileHash)) return fileHash;

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileHash + ".png").stream(
									inputStream, inputStream.available(), -1)
							.contentType(CONTENT_TYPE_PNG)
							.build());

			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(fileHash + ".webp").stream(
									inputStream, inputStream.available(), -1)
							.contentType(CONTENT_TYPE_WEBP)
							.build());

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
