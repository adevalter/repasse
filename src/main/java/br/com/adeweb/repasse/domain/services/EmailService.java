package br.com.adeweb.repasse.domain.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class EmailService {

    private JavaMailSenderImpl sender;

    public EmailService(JavaMailSenderImpl sender) {
        this.sender = sender;
        sender.setHost("mail.adeweb.com.br");
        sender.setPort(587);
        sender.setUsername("repasse@adeweb.com.br");
        sender.setPassword("Ll4LY}~vK8JQ");
        sender.getJavaMailProperties().put("mail.smtp.host", "mail.adeweb.com.br");
        sender.getJavaMailProperties().put("mail.smtp.port", "465");
        //sender.getJavaMailProperties().put("mail.debug", "true");
        sender.getJavaMailProperties().put("mail.smtp.auth", "true");
        sender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
        sender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.port", "465");
        sender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        sender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.fallback", "false");
    }



    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setBcc("hospitaldasclinicas@terra.com.br");
        helper.setSubject(subject);
        helper.setText(htmlBody,true);
        helper.setFrom("repasse@adeweb.com.br");
        helper.addInline("logoImage", new ClassPathResource("static/logohcftexto.png"));

        sender.send(mimeMessage);
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody, byte[] pdfBytes) throws MessagingException {

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setBcc("hospitaldasclinicas@terra.com.br");
        helper.setSubject(subject);
        helper.setText(htmlBody,true);
        helper.setFrom("repasse@adeweb.com.br");
        helper.addInline("logoImage", new ClassPathResource("static/logohcftexto.png"));
        helper.addAttachment("repasse.pdf", () -> new ByteArrayInputStream(pdfBytes));
        sender.send(mimeMessage);
    }
}
