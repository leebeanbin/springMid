package com.sparta.areadevelopment.config;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 메일 보내는 방식을 담고있는 클래스
 */
@Component
public class MailManager {

    /**
     * @String sender 이메일 보내는 사람의 이메일 정보
     */
    @Value("${MAIL_USERNAME}")
    private String sender;

    /**
     * @Autowired JavaMailSender 인터페이스
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 메일 보내는 메서드
     *
     * @String value 보낼 제목과 내용을 매개변수로 받음
     */
    public void send(String toMail, String title, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(new InternetAddress(sender, "하지만없죠"));
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
