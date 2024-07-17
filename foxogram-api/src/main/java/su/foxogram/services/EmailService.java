package su.foxogram.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.Code;
import su.foxogram.enums.EmailEnum;
import su.foxogram.repositories.CodeRepository;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Env;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
public class EmailService {

    private final CodeRepository codeRepository;
    private final ResourceLoader resourceLoader;
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, ResourceLoader resourceLoader, CodeRepository codeRepository) {
        this.javaMailSender = javaMailSender;
        this.resourceLoader = resourceLoader;
        this.codeRepository = codeRepository;
    }

    @Async
    public void sendEmail(String to, long id, String type, String username, String digitCode, long expiresAt, String token) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String HTMLContent = null;

        try {
            helper.setTo(to);
            helper.setFrom(Env.get("SMTP_EMAIL"));
            if (type.equals(EmailEnum.Type.DELETE.getValue())) {
                helper.setSubject("Confirm Your Account Deletion");
                HTMLContent = readHTML("delete").replace("{0}", username).replace("{1}", digitCode).replace("{2}", token);
            } else if (type.equals(EmailEnum.Type.CONFIRM.getValue())) {
                helper.setSubject("Confirm Your Email Address");
                HTMLContent = readHTML("confirm").replace("{0}", username).replace("{1}", digitCode).replace("{2}", token);
            }

            assert HTMLContent != null;
            helper.setText(HTMLContent, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        codeRepository.save(new Code(id, type, digitCode, expiresAt));
    }

    private String readHTML(String name) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:email/templates/" + name + ".html");

        try (InputStream inputStream = resource.getInputStream()) {
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}