package br.com.adeweb.repasse.domain.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class EmailService {

    public static final String STATIC_LOGOHCFTEXTO_PNG = "static/logohcftexto.png";
    private static  final String SEND_BCC = "hospitaldasclinicas@terra.com.br";
    public static final String SEND_EMAIL_FROM ="repasse@informativo.adeweb.com.br";
    public static final String LOGO_IMAGE = "logoImage";
    public static final String REPASSE_PDF = "repasse.pdf";

    private final JavaMailSender sender;

    @Autowired
    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }


    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
       // helper.setBcc(SEND_BCC);
        helper.setSubject(subject);
        helper.setText(htmlBody,true);
        helper.setFrom(SEND_EMAIL_FROM);
        helper.addInline(LOGO_IMAGE, new ClassPathResource(STATIC_LOGOHCFTEXTO_PNG));

        sender.send(mimeMessage);
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody, byte[] pdfBytes) throws MessagingException {

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setBcc(SEND_BCC);
        helper.setSubject(subject);
        helper.setText(htmlBody,true);
        helper.setFrom(SEND_EMAIL_FROM);
        helper.addInline(LOGO_IMAGE, new ClassPathResource(STATIC_LOGOHCFTEXTO_PNG));
        helper.addAttachment(REPASSE_PDF, () -> new ByteArrayInputStream(pdfBytes));
        sender.send(mimeMessage);
    }

    public void sendHtmlEmailPdf(String to, String subject, String htmlBody, String nomeArquivoPdf, byte[] pdfBytes) throws MessagingException {

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setBcc(SEND_BCC);
        helper.setSubject(subject);
        helper.setText(htmlBody,true);
        helper.setFrom(SEND_EMAIL_FROM);
        helper.addInline(LOGO_IMAGE, new ClassPathResource(STATIC_LOGOHCFTEXTO_PNG));
        helper.addAttachment(nomeArquivoPdf, () -> new ByteArrayInputStream(pdfBytes));
        sender.send(mimeMessage);
    }
}
