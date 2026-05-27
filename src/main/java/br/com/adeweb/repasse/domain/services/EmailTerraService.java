package br.com.adeweb.repasse.domain.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

@Service
public class EmailTerraService {

    public static final String STATIC_LOGOHCFTEXTO_PNG = "static/logohcftexto.png";
   // private static final String SEND_BCC = "hospitaldasclinicas@terra.com.br";
    public static final String SEND_EMAIL_FROM = "repasse@informativo.adeweb.com.br";
    public static final String REPLAY_TO_EMAIL = "hospitaldasclinicas@terra.com.br";


    public static final String LOGO_IMAGE = "logoImage";
    public static final String REPASSE_PDF = "repasse.pdf";
    public static final String HOSPITAL_DAS_CLINICAS_FERNANDOPOLIS = "Hospital Das Clínicas Fernandópolis";

    private final JavaMailSender sender;

    @Autowired
    public EmailTerraService(JavaMailSender sender) {
        this.sender = sender;
    }

    // Envio de email HTML simples
    public void sendHtmlEmail(String toEmail, String toName, String subject, String htmlBody) throws MessagingException {
        MimeMessageHelper helper = prepareMimeMessage(toEmail, toName, subject, htmlBody);
        sender.send(helper.getMimeMessage());
    }

    // Envio de email HTML com PDF anexo
    public void sendHtmlEmail(String toEmail, String toName, String subject, String htmlBody, byte[] pdfBytes) throws MessagingException {
        MimeMessageHelper helper = prepareMimeMessage(toEmail, toName, subject, htmlBody);
        helper.addAttachment(REPASSE_PDF, () -> new ByteArrayInputStream(pdfBytes));
        sender.send(helper.getMimeMessage());
    }

    // Envio de email HTML com PDF anexo e nome customizado para o PDF
    public void sendHtmlEmailPdf(String toEmail, String toName, String subject, String htmlBody, String nomeArquivoPdf, byte[] pdfBytes) throws MessagingException {
        MimeMessageHelper helper = prepareMimeMessage(toEmail, toName, subject, htmlBody);
        helper.addAttachment(nomeArquivoPdf, () -> new ByteArrayInputStream(pdfBytes));
        sender.send(helper.getMimeMessage());
    }

    // Método interno para evitar repetição de código
    private MimeMessageHelper prepareMimeMessage(String toEmail, String toName, String subject, String htmlBody) throws MessagingException {
        var mimeMessage = sender.createMimeMessage();
        var helper = new MimeMessageHelper(mimeMessage, true);

        try {
            helper.setFrom(new InternetAddress(SEND_EMAIL_FROM, HOSPITAL_DAS_CLINICAS_FERNANDOPOLIS));
            helper.setTo(new InternetAddress(toEmail, toName));
            helper.setReplyTo(new InternetAddress(REPLAY_TO_EMAIL, HOSPITAL_DAS_CLINICAS_FERNANDOPOLIS));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            helper.setFrom(SEND_EMAIL_FROM);
            helper.setTo(toEmail);
        }

      //  helper.setBcc(SEND_BCC);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addInline(LOGO_IMAGE, new ClassPathResource(STATIC_LOGOHCFTEXTO_PNG));

        return helper;
    }
}
