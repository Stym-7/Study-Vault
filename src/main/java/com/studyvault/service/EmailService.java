package com.studyvault.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOTPEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("StudyVault - Email Verification OTP");
        message.setText("Your OTP for StudyVault registration is: " + otp);
        message.setFrom("satyam1kaushik@gmail.com");

        mailSender.send(message);
        System.out.println("OTP email sent successfully to " + toEmail);
    }
}
