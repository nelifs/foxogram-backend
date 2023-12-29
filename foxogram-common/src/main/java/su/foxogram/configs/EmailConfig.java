package su.foxogram.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import su.foxogram.util.Env;

import java.util.Properties;

@Configuration
public class EmailConfig {

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(Env.get("SMTP_HOST"));
		mailSender.setPort(Integer.parseInt(Env.get("SMTP_PORT")));

		mailSender.setUsername(Env.get("SMTP_USERNAME"));
		mailSender.setPassword(Env.get("SMTP_PASSWORD"));

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "false");

		return mailSender;
	}
}