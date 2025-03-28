package bylith_project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private final JavaMailSender mailSender;

	@Value("${server.monitor.alert-email}")
	private String alertEmail;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendAlertEmail(String serverName, String url) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(alertEmail);
		message.setSubject("Server Unhealthy Alert");
		message.setText("Server " + serverName + " (" + url + ") has become unhealthy.");
		mailSender.send(message);
	}
}