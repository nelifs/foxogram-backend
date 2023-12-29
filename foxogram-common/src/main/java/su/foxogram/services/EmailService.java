package su.foxogram.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import su.foxogram.util.Env;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
public class EmailService {

	private final ResourceLoader resourceLoader;
	private final JavaMailSender javaMailSender;

	@Autowired
	public EmailService(JavaMailSender javaMailSender, ResourceLoader resourceLoader) {
		this.javaMailSender = javaMailSender;
		this.resourceLoader = resourceLoader;
	}

	public void sendConfirmEmail(String to, String username, String digitCode, String letterCode, String token) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

		try {
			helper.setTo(to);
			helper.setFrom(Env.get("SMTP_EMAIL"));
			helper.setSubject("Confirm Your Email Address");
			String htmlContent = readHTML("welcome").replace("{0}", username).replace("{1}", digitCode).replace("{2}", letterCode).replace("{3}", token);
			// String formattedContent = MessageFormat.format(htmlContent, username);
			// helper.setText("Hello, " + username + "!\nHere's your confirmation code\n" + confirmationCode);
			helper.setText(htmlContent, true);

			javaMailSender.send(mimeMessage);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
	}

	private String readHTML(String name) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:email/templates/" + name + ".html");

		try (InputStream inputStream = resource.getInputStream()) {
			Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}
}