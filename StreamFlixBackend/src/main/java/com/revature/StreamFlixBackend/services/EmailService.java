package com.revature.StreamFlixBackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/*
 * EmailService class for StreamFlix
 * This class is used to send emails to users.
 */
@Service
public class EmailService {
    /* javaMailSender is the JavaMailSender object used to send emails */
    private final JavaMailSender javaMailSender;

    /*
     * Constructor for EmailService
     * @param javaMailSender the JavaMailSender object to set
     */
    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /*
     * Sends an email to the user
     * @param to the email address to send the email to
     * @param subject the subject of the email
     * @param text the text of the email
     */
    public void sendEmail(String to, String subject, String text) {
        System.out.println("Sending email...");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("streamflixapi@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);

        System.out.println("Email sent to: " + to);
    }


}
