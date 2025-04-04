package com.abhay.assessment.java_assessment.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private void sentEmail(String to, String subject, String body) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        System.out.println(mailMessage);
        System.out.println(mailSender);

        mailSender.send(mailMessage);
    }

    public void sendResetPasswordMail(String to, String name, String token) {
        String subject = "Reset password";
        String body = "Hello " + name + ",\nHere is your reset password token.\n\n" + token;

        System.out.println(to);
        System.out.println(subject);
        System.out.println(body);

        sentEmail(to, subject, body);
    }
}
