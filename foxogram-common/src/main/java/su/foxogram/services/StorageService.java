package su.foxogram.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioAsyncClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.models.Channel;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MessageRepository;
import su.foxogram.repositories.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class StorageService {

	private static final String ALGORITHM = "MD5";

	private static final String CONTENT_TYPE_WEBP = "image/webp";

	private static final String CONTENT_TYPE_PNG = "image/png";

	private final MinioAsyncClient minioClient;

	private final UserRepository userRepository;

	private final ChannelRepository channelRepository;

	private final MessageRepository messageRepository;

	@Autowired
	public StorageService(MinioAsyncClient minioClient, UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
		this.minioClient = minioClient;
		this.userRepository = userRepository;
		this.channelRepository = channelRepository;
		this.messageRepository = messageRepository;
	}

	public String uploadFile(MultipartFile file, String bucketName) throws RuntimeException, IOException, ExecutionException, InterruptedException, NoSuchAlgorithmException {
		byte[] byteArray = file.getBytes();
		long startTime = System.currentTimeMillis();
		String fileHash = getHash(byteArray);
		log.info("Uploading file ({}) to bucket ({})", file.getOriginalFilename(), bucketName);

		if (isHashExists(fileHash)) {
			log.info("Duplicate image ({}) found. Skipping upload...", fileHash);
			return fileHash;
		}

		if (!isBucketExists(bucketName).get()) createBucket(bucketName);

		try (InputStream pngStream = new ByteArrayInputStream(byteArray);
			 InputStream webpStream = new ByteArrayInputStream(byteArray)) {


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

	private boolean isHashExists(String hash) {
		User user = userRepository.findByAvatar(hash);
		Channel channel = channelRepository.findByIcon(hash);
		List<String> attachments = getAllAttachments();

		return user != null || channel != null || attachments.contains(hash);
	}

	private void createBucket(String bucketName) {
		try {
			minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private CompletableFuture<Boolean> isBucketExists(String bucketName) throws RuntimeException {
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

	private List<String> getAllAttachments() {
		List<Message> messages = messageRepository.findAll();
		List<String> attachments = new ArrayList<>();
		for (Message message : messages) {
			if (message.getAttachments() != null) {
				attachments.addAll(message.getAttachments());
			}
		}

		return attachments;
	}
}