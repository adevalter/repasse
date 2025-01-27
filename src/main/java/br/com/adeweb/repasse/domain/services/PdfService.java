package br.com.adeweb.repasse.domain.services;


import br.com.adeweb.repasse.core.relatorios.HeaderFooterEvent;
import br.com.adeweb.repasse.core.utils.UtilStrings;
import br.com.adeweb.repasse.data.models.PagamentoDTO;
import br.com.adeweb.repasse.data.models.RelatorioPagamentoDTO;
import br.com.adeweb.repasse.domain.entities.Pagamento;
import br.com.adeweb.repasse.domain.repositories.PagamentoRepository;
import br.com.adeweb.repasse.domain.repositories.RepasseItemRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    EmailService email;

    @Autowired
    private RepasseItemRepository repasseItemRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private PagamentoService pagamentoService;


    private String emailMedico;
    private String dataDeposito;

    @Value("${pdf.directory}")
    private String pdfDirectory;

    public byte[] generatePdf(Long id) {
        double total =0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(),36, 36, 100, 36);
        try {

            List<RelatorioPagamentoDTO> pagamentos = pagamentoRepository.findByPagamentoId(id);


            this.emailMedico = pagamentos.getFirst().getEmail();
            this.dataDeposito= UtilStrings.formartaDataBr(pagamentos.getFirst().getDataDeposito().toString());

            PdfWriter writer =  PdfWriter.getInstance(document, byteArrayOutputStream);
            writer.setPageEvent(new HeaderFooterEvent());
            document.open();
           // document.setPageSize(PageSize.A4.rotate());
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("\n"));
            PdfPTable tableMedico = new PdfPTable(3);
            float[] columnWidthsMedico = {3f, 2f, 2f};


            tableMedico.setWidths(columnWidthsMedico);
            tableMedico.setWidthPercentage(100); // Tabela ocupa 100% da largura da página
            tableMedico.addCell(new PdfPCell(new Phrase("MÉDICO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase("BANCO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase("DATA DEPÓSITO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase(pagamentos.getFirst().getMedico().toUpperCase(), cellFont)));
            tableMedico.addCell(new PdfPCell(new Phrase(pagamentos.getFirst().getBancoPagamento().toUpperCase(), cellFont)));
            tableMedico.addCell(new PdfPCell(new Phrase(UtilStrings.formartaDataBr(pagamentos.getFirst().getDataDeposito().toString()), cellFont)));


            PdfPTable table = new PdfPTable(5);
            float[] columnWidths = {1f, 3f, 2f, 3f, 1f};
            table.setWidths(columnWidths);
            table.setWidthPercentage(100); // Tabela ocupa 100% da largura da página
            table.setSpacingBefore(0f);    // Sem espaçamento antes
            table.setSpacingAfter(0f);     // Sem espaçamento depois

            // Cabeçalhos
            tituloTabelaMedico(table, headerFont);


            for(RelatorioPagamentoDTO p : pagamentos)
            {
                    table.addCell(new PdfPCell(new Phrase(UtilStrings.formartaDataBr(p.getDataProcedimento().toString()), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(p.getPaciente().toUpperCase(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(p.getConvenio().toUpperCase(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(p.getProcedimento().toUpperCase(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(p.getValorProcedimento()), cellFont))).setHorizontalAlignment(Element.ALIGN_RIGHT);
                    total += p.getValorProcedimento();

            }


            // Linha de total
            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL"));
            totalCell.setColspan(4); // Mesclar as 4 primeiras colunas
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alinhar à direita
            table.addCell(totalCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("%.2f", total)));
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alinhar o valor à direita
            table.addCell(totalValueCell);

            document.add(tableMedico);
            document.add(new Paragraph("\n"));
            document.add(table );

        }catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        document.close();
        String dataPg = this.dataDeposito;

        String htmlContent = getCorpoEmailPagamento(dataPg);


        sendPdfEmail(this.emailMedico,"Extrato de Pagamento - Hospital das Clínicas Fernandópolis",htmlContent, byteArrayOutputStream.toByteArray());

            PagamentoDTO pagamentoAtual = pagamentoService.buscarById(id);
            pagamentoAtual.setStatus(3);
            pagamentoService.salvarPagamento(pagamentoAtual);




        return byteArrayOutputStream.toByteArray();
        // Adiciona os usuários ao PDF



    }

    private static String getCorpoEmailPagamento(String dataPg) {
        String htmlContent = String.format("""
            <html>
            <body>
                <p>Segue anexo, extrato de pagamento realizado em <b>%s</b>.</p>
                <p>Atenciosamente,</p>
                <br/>
                <img src="cid:logoImage" alt="Logo da Empresa" style="width:200px;"/>
            </body>
            </html>
            """ , dataPg);
        return htmlContent;
    }

    public void sendPdfEmail(String recipient, String subject, String message,byte[] pdfBytes) {
        try {

            // Cria o e-mail
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
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

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(recipient);
            helper.setBcc("hospitaldasclinicas@terra.com.br");
            helper.setSubject(subject);
            helper.setText(message,true);
            helper.setFrom("repasse@adeweb.com.br");
            helper.addInline("logoImage", new ClassPathResource("static/logohcftexto.png"));
            // Adiciona o PDF como anexo
            helper.addAttachment("repasse.pdf", () -> new ByteArrayInputStream(pdfBytes));

            // Envia o e-mail
            sender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage());
        }
    }

    public String sendEmailAviso(Long id) {

        List<RelatorioPagamentoDTO> pagamentos = pagamentoRepository.findByPagamentoId(id);
        emailMedico = pagamentos.getFirst().getEmail();
        double total =0;
        for(RelatorioPagamentoDTO p : pagamentos)
        {
            total += p.getValorProcedimento();

        }

        try {
            email.sendHtmlEmail(emailMedico, "Folha Médica", corpoEmailFolhaMedica(total));
            return "pagamentos";
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Envia E-mail de Pagamento. Utiliza como paramentro id do pagamento
     * @param pagamentoId
     * @return true
     */
    public byte[] sendEmailPagamento(Long pagamentoId) {

        List<RelatorioPagamentoDTO> pagamentos = pagamentoRepository.findByPagamentoId(pagamentoId);
        String dataPg = this.dataDeposito;
        var pdf = this.gerarPdf(pagamentoId);
        try {
            email.sendHtmlEmail(emailMedico, "Folha Médica", getCorpoEmailPagamento(dataPg),pdf);
            return pdf;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }



    private PagamentoDTO convetToDto(Pagamento pagamento) {
        return  modelMapper.map(pagamento, PagamentoDTO.class);
    }

    private Pagamento convertToPagamento(PagamentoDTO pagamentoDTO){
        return modelMapper.map(pagamentoDTO,Pagamento.class);
    }

    public String corpoEmailFolhaMedica(double valor){
        return   String.format("""
                <html lang="pt-br">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Folha Médica</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            color: #333;
                            line-height: 1.6;
                        }
                        h1 {
                            color: red;
                        }
                    </style>
                </head>
                <body>
                 <h1>AVISO IMPORTANTE</h1>
                   <p>Informamos que a Folha Médica foi fechada e o valor de <strong>R$ %.2f</strong> para a emissão da nota fiscal de prestação de serviços já foi disponibilizado.</p>
                <p>Solicitamos que verifique os valores para proceder com a emissão da nota fiscal.</p>
                <p>É fundamental que a Nota de Prestação de Serviço seja emitida o mais breve possível.</p>
                <p>Pedimos a gentileza de responder a este e-mail, anexando a nota fiscal solicitada.</p>
                <p>Caso tenha qualquer dúvida, estaremos à disposição para esclarecimentos.</p>
                <p>Lembramos que a Nota Fiscal de Prestação de Serviço deve ser anexada ao responder este e-mail.</p>
                <p>Agradecemos sua atenção e compreensão.</p>
                <p>Atenciosamente,</p>
                    <br/>
                <img src="cid:logoImage" alt="Logo da Empresa" style="width:200px;"/>
                </body>
                </html>
            """ , valor);
    }

    public byte[] gerarPdf(Long pagamentoId) {
        PdfPTable table;
        double total =0;
        int maxRowsPerPage = 48; // Defina o número máximo de linhas por página
        int currentRowCount = 0;

        int pageNumber;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4,30, 30, 80, 30);
        try {

            List<RelatorioPagamentoDTO> pagamentos = pagamentoRepository.findByPagamentoId(pagamentoId);

            this.emailMedico = pagamentos.getFirst().getEmail();

            PdfWriter writer =  PdfWriter.getInstance(document, byteArrayOutputStream);
            writer.setPageEvent(new HeaderFooterEvent());

            document.open();
            document.add(new Paragraph("\n"));


            // document.setPageSize(PageSize.A4.rotate());
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
            Font cellFontTotal = new Font(Font.FontFamily.HELVETICA,9,Font.BOLD);


            PdfPTable tableMedico = new PdfPTable(3);
            float[] columnWidthsMedico = {3f, 2f, 2f};

            document.add(new Paragraph("\n"));
            tableMedico.setWidths(columnWidthsMedico);
            tableMedico.setWidthPercentage(100); // Tabela ocupa 100% da largura da página
            tableMedico.addCell(new PdfPCell(new Phrase("MÉDICO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase("BANCO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase("DATA DEPÓSITO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase(pagamentos.getFirst().getMedico(), cellFont)));
            tableMedico.addCell(new PdfPCell(new Phrase(pagamentos.getFirst().getBancoPagamento(), cellFont)));
            tableMedico.addCell(new PdfPCell(new Phrase("", cellFont)));


            table = new PdfPTable(5);
            float[] columnWidths = {100, 350, 120, 270, 90};
            table.setWidths(columnWidths);
            table.setWidthPercentage(100); // Tabela ocupa 100% da largura da página
            table.setSpacingBefore(0f);    // Sem espaçamento antes
            table.setSpacingAfter(0f);     // Sem espaçamento depois


            // Cabeçalhos
            tituloTabelaMedico(table, headerFont);


            for(RelatorioPagamentoDTO p : pagamentos)
            {

                if (currentRowCount >= maxRowsPerPage) {
                    document.add(table );
                    document.newPage();
                    table = new PdfPTable(5);
                    table.setWidths(columnWidths);
                    table.setWidthPercentage(100); // Tabela ocupa 100% da largura da página
                    table.setSpacingBefore(0f);    // Sem espaçamento antes
                    table.setSpacingAfter(0f);
                    document.add(new Paragraph("\n"));
                    tituloTabelaMedico(table, headerFont);// Nova página
                    currentRowCount = 0; // Reinicia o contador de linhas
                }

                table.addCell(new PdfPCell(new Phrase(UtilStrings.formartaDataBr(p.getDataProcedimento().toString()), cellFont)));
                table.addCell(new PdfPCell(new Phrase(p.getPaciente().toUpperCase(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(p.getConvenio().toUpperCase(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(p.getProcedimento().toUpperCase(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(String.format("%.2f",p.getValorProcedimento()), cellFont))).setHorizontalAlignment(Element.ALIGN_RIGHT);
                total += p.getValorProcedimento();
                currentRowCount++;

            }


            // Linha de total
            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL",cellFont));
            totalCell.setColspan(4); // Mesclar as 4 primeiras colunas

            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alinhar à direita
            table.addCell(totalCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("%.2f", total,cellFontTotal)));
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alinhar o valor à direita
            table.addCell(totalValueCell);
            document.add(new Paragraph("\n"));
            document.add(tableMedico);
            document.add(new Paragraph("\n"));
            document.add(table );

        }catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
        // Adiciona os usuários ao PDF



    }

    private static void tituloTabelaMedico(PdfPTable table, Font headerFont) {
        table.addCell(new PdfPCell(new Phrase("Data", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Paciente", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Convênio", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Procedimento", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Valor", headerFont)));
    }

}

