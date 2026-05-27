package br.com.adeweb.repasse.domain.services;


import br.com.adeweb.repasse.core.relatorios.HeaderFooterPageEvent;
import br.com.adeweb.repasse.core.utils.UtilStrings;
import br.com.adeweb.repasse.data.models.DespesaMedicoDTO;
import br.com.adeweb.repasse.data.models.RelatorioPagamentoDTO;
import br.com.adeweb.repasse.domain.entities.DespesaMedico;
import br.com.adeweb.repasse.domain.entities.Orcamento;
import br.com.adeweb.repasse.domain.repositories.DespesaMedicoRepository;
import br.com.adeweb.repasse.domain.repositories.OrcamentoRepository;
import br.com.adeweb.repasse.domain.repositories.PagamentoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class PdfService {

    private final EmailTerraService emailService;
    private final PagamentoRepository pagamentoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final DespesaMedicoRepository despesaMedicoRepository;

    private String emailMedico;
    private String nomeMedico;


    @Value("${pdf.directory}")
    private String pdfDirectory;

    public PdfService(EmailTerraService emailService, PagamentoRepository pagamentoRepository, OrcamentoRepository orcamentoRepository, DespesaMedicoRepository despesaMedicoRepository) {

        this.emailService = emailService;
        this.pagamentoRepository = pagamentoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.despesaMedicoRepository = despesaMedicoRepository;
    }

    private static String getCorpoEmailPagamento(String dataPg) {
        return String.format("""
                <html>
                <body>
                    <p>Segue anexo, extrato de pagamento realizado em <b>%s</b>.</p>
                    <p>Atenciosamente,</p>
                    <br/>
                    <img src="cid:logoImage" alt="Logo da Empresa" style="width:200px;"/>
            </body>
            </html>
            """ , dataPg);
    }


    public String sendEmailAviso(Long id) {

        List<RelatorioPagamentoDTO> pagamentos = pagamentoRepository.findByPagamentoId(id);
        emailMedico = pagamentos.getFirst().getEmail();
        nomeMedico = pagamentos.getFirst().getMedico();
        double total =0;
        for(RelatorioPagamentoDTO p : pagamentos)
        {
            total += p.getValorProcedimento();

        }

        try {
            emailService.sendHtmlEmail(emailMedico,nomeMedico, "Demonstrativo de Pagamento", corpoEmailFolhaMedica(total));
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
    public byte[] sendEmailPagamento(Long pagamentoId, boolean enviarEmail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataPg = null;
        List<RelatorioPagamentoDTO> pagamentos = pagamentoRepository.findByPagamentoId(pagamentoId);
        nomeMedico = pagamentos.getFirst().getMedico();
        if (pagamentos.getFirst().getDataDeposito() != null) {
            dataPg = pagamentos.getFirst().getDataDeposito().format(formatter);
        }
        var pdf = this.gerarPdf( pagamentoId);
        try {
            if(enviarEmail)
                emailService.sendHtmlEmail(emailMedico, nomeMedico,"Demonstrativo de Pagamento", getCorpoEmailPagamento(dataPg),pdf);
            return pdf;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

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

    public byte[] enviarOrcamento(Long orcamentoId, boolean email) throws MessagingException {
        Optional<Orcamento> orcamento = orcamentoRepository.findById(orcamentoId);
        Orcamento o = orcamento.orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        var gerarPdf = this.gerarOrcamentoPdf(o);
        if(email){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = o.getCreatedAt().format(formatter);
            emailService.sendHtmlEmailPdf(o.getEmail(),o.getNome(),"Orçamento",getCorpoEmailOrcamento(dataFormatada),"orçamento.pdf",gerarPdf);
        }
        return  gerarPdf;
    }

    public byte[] gerarOrcamentoPdf(Orcamento orcamento) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 80, 30);

        try {
            String textoDoBanco = orcamento.getDescricao();

            List<String> palavrasChave = Arrays.asList(
                    "PROCEDIMENTO CIRÚRGICO:", "MÉDICO(A) RESPONSÁVEL:", "CIRURGIÃO:", "AUXILIAR:", "PEDIATRA:", "ANESTESISTA:","INSTRUMENTADOR:",
                    "TOTAL EQUIPE MÉDICA: ","OBS:","DESPESAS HOSPITALARES", "MATERIAIS ESPECIAIS","ANATOMOPATOLÓGICO",
                    "TOTAL:","TOTAL GERAL DO PROCEDIMENTO:","INCLUSO:","NÃO INCLUSO: ","OBSERVAÇÕES:",
                    "COMPLICAÇÕES E INTERCORRÊNCIAS:", "ORÇAMENTO É VÁLIDO POR 30 DIAS.");

            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            writer.setPageEvent(new HeaderFooterPageEvent("ORÇAMENTO MÉDICO HOSPITALAR","src/main/resources/static/logohcf.png"));
            document.open();

            document.add(new Paragraph("\n"));

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font fonteNormal = new Font(Font.FontFamily.HELVETICA, 11);
            Font fonteNegrito = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font fonteTotalGeral = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fonteMenor = new Font(Font.FontFamily.HELVETICA, 8,Font.BOLD);

            // Tabela com dados do cliente
            PdfPTable tableDadosCliente = new PdfPTable(2);
            tableDadosCliente.setWidths(new float[]{1.5f, 1f});
            tableDadosCliente.setWidthPercentage(100);


            PdfPCell cellNome = new PdfPCell(new Phrase(String.format("NOME PACIENTE:  %s", orcamento.getNome().toUpperCase()), headerFont));
            tableDadosCliente.addCell(cellNome);

            PdfPCell cellSexo = new PdfPCell(new Phrase(String.format("SEXO:  %s", orcamento.getSexo().toUpperCase()), headerFont));
            tableDadosCliente.addCell(cellSexo);

            PdfPCell cellEmail = new PdfPCell(new Phrase(String.format("E-MAIL:  %s", orcamento.getEmail().toUpperCase()), headerFont));
            tableDadosCliente.addCell(cellEmail);

            PdfPCell cellCelular = new PdfPCell(new Phrase(String.format("CELULAR:  %s", orcamento.getCelular().toUpperCase()), headerFont));
            tableDadosCliente.addCell(cellCelular);

            document.add(tableDadosCliente);


            // Cria tabela principal com 1 coluna (conteúdo + observação + assinatura)
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setSplitLate(false);

            // Monta parágrafo com negrito para palavras-chave
            Paragraph paragrafoComNegrito = new Paragraph();
            paragrafoComNegrito.setLeading(12f); // ajuste aqui!

            palavrasChave.sort((a, b) -> Integer.compare(b.length(), a.length()));

            for (String linha : textoDoBanco.split("\n")) {
                boolean achouChave = false;

                if (linha.toUpperCase().contains("TOTAL GERAL DO PROCEDIMENTO:")) {
                    paragrafoComNegrito.add(new Chunk(linha, fonteTotalGeral));
                    paragrafoComNegrito.add(Chunk.NEWLINE);

                    continue;
                }

                for (String palavraChave : palavrasChave) {
                    int index = linha.indexOf(palavraChave);
                    if (index != -1) {
                        Chunk chunkNegrito = new Chunk(palavraChave, fonteNegrito);
                        paragrafoComNegrito.add(chunkNegrito);

                        String restante = linha.substring(index + palavraChave.length()).trim();
                        paragrafoComNegrito.add(new Chunk(" " + restante, fonteNormal));
                        paragrafoComNegrito.add(Chunk.NEWLINE);
                        achouChave = true;
                        break;
                    }
                }

                if (!achouChave) {
                    paragrafoComNegrito.add(new Chunk(linha, fonteNormal));
                    paragrafoComNegrito.add(Chunk.NEWLINE);
                }
            }
            // Célula com o texto principal
            PdfPCell cellTextoPrincipal = new PdfPCell();
            cellTextoPrincipal.addElement(paragrafoComNegrito);
            cellTextoPrincipal.setBorder(Rectangle.BOX);
            table.addCell(cellTextoPrincipal);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = orcamento.getCreatedAt().format(formatter);

            Phrase frase = new Phrase();
            frase.add(new Chunk("RESPONSÁVEL PELO ORÇAMENTO: ", fonteNegrito));
            frase.add(new Chunk("TATIANA DOS SANTOS \n", fonteNormal));
            frase.add(new Chunk("DATA: " + dataFormatada + "\n", fonteNegrito));


            if(orcamento.getStatus() == 2){
                String dataAprovadoFormatada = orcamento.getAprovadoEm().format(formatter);
                frase.add(new Chunk("\nAPROVADO EM: ", fonteNegrito));
                frase.add(new Chunk(dataAprovadoFormatada + "\n", fonteNormal));
            }
            frase.add(new Chunk(orcamento.getChave(), fonteMenor ));

            PdfPCell cellAssinatura = new PdfPCell(frase);
            cellAssinatura.setPadding(12f);
            table.addCell(cellAssinatura);
            document.add(table);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return byteArrayOutputStream.toByteArray();
    }


    public byte[] gerarPdf(Long pagamentoId) {
        PdfPTable table;
        double total =0;

        int currentRowCount = 0;

        int pageNumber;
        long idRepasse =0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4,30, 30, 85, 30);
        try {
            DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
            List<RelatorioPagamentoDTO> pagamentos = pagamentoRepository.findByPagamentoId(pagamentoId);
            idRepasse = pagamentos.getFirst().getRepasseId();
            this.emailMedico = pagamentos.getFirst().getEmail();

            PdfWriter writer =  PdfWriter.getInstance(document, byteArrayOutputStream);
            writer.setPageEvent(new HeaderFooterPageEvent("FORMULÁRIO DE REPASSE TERCEIRO","src/main/resources/static/logohcf.png"));



            // document.setPageSize(PageSize.A4.rotate());
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
            Font cellFontConvenio = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            Font cellFontTotal = new Font(Font.FontFamily.HELVETICA,9,Font.BOLD);


            PdfPTable tableMedico = new PdfPTable(3);
            document.open();
            //  document.add(new Paragraph("\n"));
            tableMedico.setWidthPercentage(100);

            // Permite quebra correta mesmo com linha alta/múltiplas linhas
            tableMedico.setSplitRows(true);
            tableMedico.setSplitLate(false);
            float[] columnWidthsMedico = {3f, 2f, 2f};


            tableMedico.setWidths(columnWidthsMedico);
            tableMedico.setWidthPercentage(100); // Tabela ocupa 100% da largura da página
            tableMedico.addCell(new PdfPCell(new Phrase("MÉDICO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase("BANCO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase("DATA DEPÓSITO", headerFont)));
            tableMedico.addCell(new PdfPCell(new Phrase(pagamentos.getFirst().getMedico().toUpperCase(), cellFont)));

            if(pagamentos.getFirst().getBancoPagamento() != null) {
                tableMedico.addCell(new PdfPCell(new Phrase(pagamentos.getFirst().getBancoPagamento().toUpperCase(), cellFont)));
                tableMedico.addCell(new PdfPCell(new Phrase(UtilStrings.formartaDataBr(pagamentos.getFirst().getDataDeposito().toString()), cellFont)));
            }else{
                tableMedico.addCell(new PdfPCell(new Phrase("", cellFont)));
                tableMedico.addCell(new PdfPCell(new Phrase("", cellFont)));
            }

            document.add(tableMedico);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            table = new PdfPTable(5);
            float[] columnWidths = {90, 350, 140, 260, 90};
            table.setWidths(columnWidths);
            table.setWidthPercentage(100); // Tabela ocupa 100% da largura da página
            table.setSpacingBefore(0f);    // Sem espaçamento antes
            table.setSpacingAfter(0f);     // Sem espaçamento depois


            // Cabeçalhos
            tituloTabelaMedico(table, headerFont);


            for(RelatorioPagamentoDTO p : pagamentos)
            {

                if (writer.getVerticalPosition(true) < 120) {

                    // encerra a tabela atual na página antiga
                    document.add(table);

                    // cria nova página
                    document.newPage();

                    // distância do topo para não sobrescrever o cabeçalho do PageEvent
                    tableMedico.setSpacingBefore(0f);
                    document.add(tableMedico);
                    document.add(new Paragraph("\n"));

                    // cria nova tabela
                    table = new PdfPTable(5);
                    table.setWidths(columnWidths);
                    table.setWidthPercentage(100);
                 //   table.setSpacingBefore(0f);

                    // recria os títulos da tabela
                    tituloTabelaMedico(table, headerFont);

                    currentRowCount = 0;
                }
                String valorFormatado = df.format(p.getValorProcedimento());

                table.addCell(new PdfPCell(new Phrase(UtilStrings.formartaDataBr(p.getDataProcedimento().toString()), cellFont)));
                table.addCell(new PdfPCell(new Phrase(p.getPaciente().toUpperCase(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(p.getConvenio().toUpperCase(), cellFontConvenio)));
                table.addCell(new PdfPCell(new Phrase(p.getProcedimento().toUpperCase(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(valorFormatado, cellFont))).setHorizontalAlignment(Element.ALIGN_RIGHT);
                total += p.getValorProcedimento();
                currentRowCount++;

            }


            // Linha de total
            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL",cellFont));
            totalCell.setColspan(4); // Mesclar as 4 primeiras colunas

            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alinhar à direita
            table.addCell(totalCell);
            long valorArredondado = Math.round(total);
            PdfPCell totalValueCell = new PdfPCell(new Phrase(df.format(valorArredondado),cellFontTotal));
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alinhar o valor à direita
            table.addCell(totalValueCell);

;
            document.add(table );

            List<DespesaMedico> despesas = despesaMedicoRepository.findByRepasseId(idRepasse);

            if (despesas != null && !despesas.isEmpty()) {

                gerarTabelaDespesas(document, writer, despesas, df,valorArredondado);
            }

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

    private static String getCorpoEmailOrcamento(String data) {
        return String.format("""
                <html>
                <body>
                    <p>Olá! Conforme solicitado, segue em anexo orçamento, válido por 30 dias a partir da data: <b>%s</b>.</p>
                    <p>Qualquer dúvida, estou à disposição!</p>
                    <p>Atenciosamente,</p>
                    <br/>
                    <img src="cid:logoImage" alt="Logo da Empresa" style="width:200px;"/>
            </body>
            </html>
            """ , data);
    }

    private void gerarTabelaDespesas(Document document,
                                     PdfWriter writer,
                                     List<DespesaMedico> despesas,
                                     DecimalFormat df,
                                     double totalCredito) throws DocumentException {

        if (despesas == null || despesas.isEmpty()) {
            return;
        }

        // ===== TÍTULO DESPESAS =====


        double totalDespesas = 0;

        String descricaoDespesa = "";


        for (DespesaMedico d : despesas) {

            totalDespesas += d.getValor();
            descricaoDespesa = d.getDescricao();
        }

        // CALCULA TOTAL LÍQUIDO
        double totalFinal = totalCredito - totalDespesas;

        // ===== TABELA DE TOTAL FINAL =====
//        document.add(new Paragraph("\nTOTAL LÍQUIDO",
//                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        // ===== TABELA VALOR A RECEBER =====
        document.add(new Paragraph("\n"));

        PdfPTable resumo = new PdfPTable(2);
        resumo.setWidthPercentage(100);
        resumo.setWidths(new float[]{4f, 2f});

// Fontes
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

// ===== TÍTULO =====
        PdfPCell titulo = new PdfPCell(new Phrase("VALOR A RECEBER", tituloFont));
        titulo.setColspan(2);
        titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
        titulo.setPadding(6);
        titulo.setBorder(Rectangle.BOX);
        resumo.addCell(titulo);

// ===== LINHA: VALOR TOTAL BRUTO =====
        PdfPCell brutoLabel = new PdfPCell(new Phrase("VALOR TOTAL BRUTO", labelFont));
        brutoLabel.setPadding(5);
        resumo.addCell(brutoLabel);

        PdfPCell brutoValor = new PdfPCell(new Phrase("R$ " + df.format(totalCredito), valueFont));
        brutoValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        brutoValor.setPadding(5);
        resumo.addCell(brutoValor);

// ===== LINHA: VALOR TOTAL DESPESAS =====
        PdfPCell despLabel = new PdfPCell(new Phrase(descricaoDespesa.toUpperCase(), labelFont));
        despLabel.setPadding(5);
        resumo.addCell(despLabel);

        PdfPCell despValor = new PdfPCell(new Phrase("R$ " + df.format(totalDespesas), valueFont));
        despValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        despValor.setPadding(5);
        resumo.addCell(despValor);

// ===== LINHA FINAL: VALOR A RECEBER =====
        PdfPCell totalLabel = new PdfPCell(new Phrase("VALOR A RECEBER", totalFont));
        totalLabel.setPadding(6);
        totalLabel.setBorderWidthTop(2f);
        resumo.addCell(totalLabel);

        PdfPCell totalValor = new PdfPCell(new Phrase("R$ " + df.format(totalFinal), totalFont));
        totalValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValor.setPadding(6);
        totalValor.setBorderWidthTop(2f);
        resumo.addCell(totalValor);

        document.add(resumo);

    }
}

