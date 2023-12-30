package su.foxogram.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.EmailVerification;
import su.foxogram.enums.EmailEnum;
import su.foxogram.repositories.EmailVerifyRepository;
import su.foxogram.util.Env;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
public class EmailService {

	private final EmailVerifyRepository emailVerifyRepository;
	private final ResourceLoader resourceLoader;
	private final JavaMailSender javaMailSender;

	@Autowired
	public EmailService(JavaMailSender javaMailSender, ResourceLoader resourceLoader, EmailVerifyRepository emailVerifyRepository) {
		this.javaMailSender = javaMailSender;
		this.resourceLoader = resourceLoader;
		this.emailVerifyRepository = emailVerifyRepository;
	}

	public void sendEmail(String to, long id, String type, String username, String digitCode, String letterCode, String token) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		String HTMLContent = null;

		try {
			helper.setTo(to);
			helper.setFrom(Env.get("SMTP_EMAIL"));
			if (type.equals(EmailEnum.Type.DELETE.getValue())) {
				helper.setSubject("Confirm Your Account Deletion");
				HTMLContent = readHTML("delete").replace("{0}", username).replace("{1}", digitCode).replace("{2}", letterCode).replace("{3}", token);
			} else if (type.equals(EmailEnum.Type.CONFIRM.getValue())) {
				helper.setSubject("Confirm Your Email Address");
				HTMLContent = readHTML("confirm").replace("{0}", username).replace("{1}", digitCode).replace("{2}", letterCode).replace("{3}", token);
			}

			assert HTMLContent != null;
			helper.setText(HTMLContent, true);

			javaMailSender.send(mimeMessage);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}

		emailVerifyRepository.save(new EmailVerification(id, type, digitCode, letterCode));
	}

	private String readHTML(String name) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:email/templates/" + name + ".html");

		try (InputStream inputStream = resource.getInputStream()) {
			Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}
}