package com.devteria.notification.service;

import com.devteria.notification.dto.request.BookingConfirmedEmailRequest;
import com.devteria.notification.dto.request.EmailRequest;
import com.devteria.notification.dto.request.SendEmailRequest;
import com.devteria.notification.dto.request.Sender;
import com.devteria.notification.dto.response.EmailResponse;
import com.devteria.notification.exception.AppException;
import com.devteria.notification.exception.ErrorCode;
import com.devteria.notification.repository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
//    EmailClient emailClient;
//    @NonFinal
//    @Value("${notification.email.brevo-apikey}")
//    String apiKey;

    JavaMailSender mailSender;
    TemplateEngine templateEngine;

//    public EmailResponse sendEmail(SendEmailRequest request) {
//        EmailRequest emailRequest = EmailRequest.builder()
//                .sender(Sender.builder()
//                        .name("ParkingChargingSys")
//                        .email("noreply@brevo.com")
//                        .build())
//                .to(List.of(request.getTo()))
//                .subject(request.getSubject())
//                .htmlContent(request.getHtmlContent())
//                .build();
//        try {
//            return emailClient.sendEmail(apiKey, emailRequest);
//        } catch (FeignException e){
//            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
//        }
//    }

    public void sendEmail(SendEmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getTo().getEmail());
        message.setSubject(request.getSubject());
        message.setText(request.getHtmlContent());
        message.setFrom("kieuquocanh4@gmail.com");

        mailSender.send(message);
    }



    public void sendOtpEmail(String to, String name, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Load Thymeleaf template
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("otp", otp);
            String htmlContent = templateEngine.process("otp-email", context);

            helper.setTo(to);
            helper.setSubject("Your OTP Code");
            helper.setText(htmlContent, true); // true = send as HTML
            helper.setFrom("POLO@service.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


    public void sendBookingConfirmedEmail(String to, BookingConfirmedEmailRequest bookingData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Load Thymeleaf template
            Context context = new Context();
            context.setVariable("name", to);
            context.setVariable("otp", 9999);
            String htmlContent = templateEngine.process("otp-email", context);

            helper.setTo(to);
            helper.setSubject("Your OTP Code");
            helper.setText(htmlContent, true); // true = send as HTML
            helper.setFrom("POLO@service.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendBookingAboutToHappenEmail(String to, String bookingId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Load Thymeleaf template
            Context context = new Context();
            context.setVariable("name", to);
            context.setVariable("otp", 1111);
            String htmlContent = templateEngine.process("otp-email", context);

            helper.setTo(to);
            helper.setSubject("Your OTP Code");
            helper.setText(htmlContent, true); // true = send as HTML
            helper.setFrom("POLO@service.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
