package com.smart.service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String from, String to, String subject, String message) {

		boolean flag = false;

		// String from = "iamapurrv@gmail.com";
		String host = "smtp.gmail.com";

		// get System properties
		Properties properties = System.getProperties();
		System.out.println("properties " + properties);

		// properties.put("mail.smtp.host", host);
		// properties.put("mail.smtp.port", "465"); // 587
		// properties.put("mail.smtp.ssl.enable", "true");
		// properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.host", host);

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("iamapurrv", "");
			}
		});

		session.setDebug(true);

		Message mimeMessage = new MimeMessage(session);
		try {
			// mimeMessage.setFrom(from);
			mimeMessage.setFrom(new InternetAddress(from));
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			mimeMessage.setSubject(subject);
			// mimeMessage.setText(message);
			mimeMessage.setContent(message, "text/html");

			Transport.send(mimeMessage);
			System.out.println("OTP sent.");
			flag = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return flag;
	}

}
