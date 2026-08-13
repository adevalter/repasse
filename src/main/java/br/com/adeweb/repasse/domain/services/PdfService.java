package br.com.adeweb.repasse.domain.services;


import br.com.adeweb.repasse.core.relatorios.HeaderFooterPageEvent;
import br.com.adeweb.repasse.core.utils.UtilStrings;
import br.com.adeweb.repasse.data.models.DespesaMedicoDTO;
import br.com.adeweb.repasse.data.models.RelatorioPagamentoDTO;
import br.com.adeweb.repasse.domain.entities.DespesaMedico;
import br.com.adeweb.repasse.domain.entities.Orcamento;
import br.com.adeweb.repasse.domain.entities.OrcamentoItem;
import br.com.adeweb.repasse.domain.enums.SecaoOrcamentoItem;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
        Orcamento o = orcamentoRepository.findByIdWithItens(orcamentoId)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        var gerarPdf = this.gerarOrcamentoPdf(o);
        if (email) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = o.getCreatedAt().format(formatter);
            emailService.sendHtmlEmailPdf(o.getEmail(), o.getNome(), "Orçamento", getCorpoEmailOrcamento(dataFormatada), "orçamento.pdf", gerarPdf);
        }
        return gerarPdf;
    }

    public byte[] gerarOrcamentoPdf(Orcamento orcamento) {
        boolean estruturado = orcamento.getTipo() != null
                || (orcamento.getItens() != null && !orcamento.getItens().isEmpty());

        if (estruturado) {
            return gerarOrcamentoPdfEstruturado(orcamento);
        }
        return gerarOrcamentoPdfLegado(orcamento);
    }

    private byte[] gerarOrcamentoPdfEstruturado(Orcamento orcamento) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Layout compacto + margem superior maior (área não imprimível da impressora)
        Document document = new Document(PageSize.A4, 24, 24, 88, 24);

        try {
            DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            writer.setPageEvent(new HeaderFooterPageEvent(
                    "ORÇAMENTO MÉDICO HOSPITALAR",
                    "src/main/resources/static/logohcf.png",
                    true));
            document.open();

            BaseColor azulEscuro = new BaseColor(30, 60, 114);
            BaseColor azulSuave = new BaseColor(241, 245, 249);
            BaseColor cinzaBorda = new BaseColor(226, 232, 240);
            BaseColor cinzaTexto = new BaseColor(51, 65, 85);
            BaseColor destaqueTotal = new BaseColor(226, 232, 240);

            Font tituloSecao = new Font(Font.FontFamily.HELVETICA, 7.5f, Font.BOLD, azulEscuro);
            Font rotulo = new Font(Font.FontFamily.HELVETICA, 6.5f, Font.NORMAL, new BaseColor(100, 116, 139));
            Font valorCampo = new Font(Font.FontFamily.HELVETICA, 7.5f, Font.BOLD, cinzaTexto);
            Font textoNormal = new Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, cinzaTexto);
            Font textoNegrito = new Font(Font.FontFamily.HELVETICA, 7f, Font.BOLD, cinzaTexto);
            Font textoTotal = new Font(Font.FontFamily.HELVETICA, 7.5f, Font.BOLD, azulEscuro);
            Font textoPequeno = new Font(Font.FontFamily.HELVETICA, 6.5f, Font.NORMAL, cinzaTexto);

            document.add(montarCabecalhoPacienteModerno(orcamento, rotulo, valorCampo, azulSuave, cinzaBorda));

            PdfPTable infoProc = new PdfPTable(2);
            infoProc.setWidthPercentage(100);
            infoProc.setWidths(new float[]{1f, 1f});
            infoProc.setSpacingBefore(3f);
            infoProc.setSpacingAfter(3f);
            infoProc.addCell(celulaCampoModerno("Procedimento cirúrgico", safe(orcamento.getProcedimentoCirurgico()), rotulo, valorCampo, cinzaBorda));
            infoProc.addCell(celulaCampoModerno("Equipe médica", safe(orcamento.getEquipeMedica()), rotulo, valorCampo, cinzaBorda));
            document.add(infoProc);

            List<OrcamentoItem> itens = orcamento.getItens() != null ? orcamento.getItens() : List.of();
            List<OrcamentoItem> equipe = filtrarSecao(itens, SecaoOrcamentoItem.EQUIPE);
            List<OrcamentoItem> hospitalar = filtrarSecao(itens, SecaoOrcamentoItem.HOSPITALAR);
            List<OrcamentoItem> material = filtrarSecao(itens, SecaoOrcamentoItem.MATERIAL);

            BigDecimal totalEquipe = BigDecimal.ZERO;
            BigDecimal totalHospitalar = BigDecimal.ZERO;
            BigDecimal totalMaterial = BigDecimal.ZERO;

            if (!equipe.isEmpty()) {
                document.add(tituloSecaoBarra("Equipe médica", tituloSecao, azulSuave));
                PdfPTable tabelaEquipe = criarTabelaItens();
                for (OrcamentoItem item : equipe) {
                    BigDecimal valor = valorItem(item);
                    totalEquipe = totalEquipe.add(valor);
                    adicionarLinhaItem(tabelaEquipe, item.getDescricao(), formatarMoeda(df, valor), textoNormal, textoNegrito, cinzaBorda);
                }
                adicionarLinhaTotal(tabelaEquipe, "Total equipe", "R$ " + formatarMoeda(df, totalEquipe), textoTotal, destaqueTotal);
                document.add(tabelaEquipe);
            }

            if (orcamento.getObsPagamento() != null && !orcamento.getObsPagamento().isBlank()) {
                PdfPTable aviso = blocoAviso("Condições de pagamento", orcamento.getObsPagamento(), tituloSecao, textoNormal, azulSuave, cinzaBorda);
                aviso.setSpacingBefore(2f);
                aviso.setSpacingAfter(2f);
                document.add(aviso);
            }

            if (!hospitalar.isEmpty()) {
                document.add(tituloSecaoBarra("Despesas hospitalares", tituloSecao, azulSuave));
                PdfPTable tabelaHosp = criarTabelaItens();
                for (OrcamentoItem item : hospitalar) {
                    BigDecimal valor = valorItem(item);
                    totalHospitalar = totalHospitalar.add(valor);
                    adicionarLinhaItem(tabelaHosp, item.getDescricao(), "R$ " + formatarMoeda(df, valor), textoNormal, textoNegrito, cinzaBorda);
                }
                adicionarLinhaTotal(tabelaHosp, "Total hospitalar", "R$ " + formatarMoeda(df, totalHospitalar), textoTotal, destaqueTotal);
                document.add(tabelaHosp);
            }

            if (!material.isEmpty()) {
                document.add(tituloSecaoBarra("Materiais especiais", tituloSecao, azulSuave));
                PdfPTable tabelaMat = criarTabelaItens();
                for (OrcamentoItem item : material) {
                    BigDecimal valor = valorItem(item);
                    totalMaterial = totalMaterial.add(valor);
                    adicionarLinhaItem(tabelaMat, item.getDescricao(), "R$ " + formatarMoeda(df, valor), textoNormal, textoNegrito, cinzaBorda);
                }
                adicionarLinhaTotal(tabelaMat, "Total materiais", "R$ " + formatarMoeda(df, totalMaterial), textoTotal, destaqueTotal);
                document.add(tabelaMat);
            }

            BigDecimal totalGeral = totalEquipe.add(totalHospitalar).add(totalMaterial);

            PdfPTable condicoes = blocoAviso(
                    "Condições de pagamento",
                    OrcamentoTextos.CONDICOES_PAGAMENTO,
                    tituloSecao,
                    textoNormal,
                    azulSuave,
                    cinzaBorda);
            condicoes.setSpacingBefore(3f);
            condicoes.setSpacingAfter(2f);
            document.add(condicoes);

            PdfPTable totalBox = blocoTotalGeral("Total geral", "R$ " + formatarMoeda(df, totalGeral), textoTotal, azulEscuro);
            totalBox.setSpacingBefore(2f);
            totalBox.setSpacingAfter(2f);
            document.add(totalBox);

            document.add(gradeTextosOrcamento(
                    orcamento.getIncluso(),
                    orcamento.getNaoIncluso(),
                    orcamento.getComplicacoes(),
                    orcamento.getObservacoes(),
                    tituloSecao,
                    textoNormal,
                    textoNegrito,
                    cinzaBorda));

            document.add(montarAssinaturaModerna(orcamento, textoNegrito, textoNormal, textoPequeno, cinzaBorda, azulSuave));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return byteArrayOutputStream.toByteArray();
    }

    private Paragraph espaco(float altura) {
        Paragraph p = new Paragraph(" ");
        p.setLeading(altura);
        return p;
    }

    private PdfPTable montarCabecalhoPacienteModerno(Orcamento orcamento, Font rotulo, Font valor,
                                                    BaseColor fundo, BaseColor borda) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.6f, 0.5f, 1.5f, 1f});
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);

        table.addCell(celulaCampoModerno("Paciente", safe(orcamento.getNome()), rotulo, valor, borda, fundo));
        table.addCell(celulaCampoModerno("Sexo", safe(orcamento.getSexo()), rotulo, valor, borda, fundo));
        table.addCell(celulaCampoModerno("E-mail", safe(orcamento.getEmail()), rotulo, valor, borda, fundo));
        table.addCell(celulaCampoModerno("Celular", safe(orcamento.getCelular()), rotulo, valor, borda, fundo));
        return table;
    }

    private PdfPCell celulaCampoModerno(String label, String value, Font rotulo, Font valorCampo, BaseColor borda) {
        return celulaCampoModerno(label, value, rotulo, valorCampo, borda, BaseColor.WHITE);
    }

    private PdfPCell celulaCampoModerno(String label, String value, Font rotulo, Font valorCampo,
                                        BaseColor borda, BaseColor fundo) {
        Paragraph p = new Paragraph();
        p.setLeading(8f);
        p.add(new Chunk(label.toUpperCase() + "\n", rotulo));
        p.add(new Chunk(value.isBlank() ? "—" : value, valorCampo));
        PdfPCell cell = new PdfPCell(p);
        cell.setPadding(3.5f);
        cell.setBorderColor(borda);
        cell.setBorderWidth(0.6f);
        cell.setBackgroundColor(fundo);
        return cell;
    }

    private PdfPTable tituloSecaoBarra(String titulo, Font fonte, BaseColor fundo) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2f);
        table.setSpacingAfter(0.5f);
        PdfPCell cell = new PdfPCell(new Phrase(titulo.toUpperCase(), fonte));
        cell.setPadding(3f);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(fundo);
        table.addCell(cell);
        return table;
    }

    private PdfPTable criarTabelaItens() throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3.2f, 1f});
        table.setSpacingAfter(1f);
        return table;
    }

    private void adicionarLinhaItem(PdfPTable table, String descricao, String valor,
                                    Font fonteDesc, Font fonteValor, BaseColor borda) {
        PdfPCell cDesc = new PdfPCell(new Phrase(descricao != null ? descricao : "", fonteDesc));
        cDesc.setPadding(3f);
        cDesc.setBorderColor(borda);
        cDesc.setBorderWidth(0.5f);

        PdfPCell cValor = new PdfPCell(new Phrase(valor, fonteValor));
        cValor.setPadding(3f);
        cValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cValor.setBorderColor(borda);
        cValor.setBorderWidth(0.5f);

        table.addCell(cDesc);
        table.addCell(cValor);
    }

    private void adicionarLinhaTotal(PdfPTable table, String label, String valor,
                                     Font fonte, BaseColor fundo) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, fonte));
        cLabel.setPadding(3.5f);
        cLabel.setBorder(Rectangle.NO_BORDER);
        cLabel.setBackgroundColor(fundo);

        PdfPCell cValor = new PdfPCell(new Phrase(valor, fonte));
        cValor.setPadding(3.5f);
        cValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cValor.setBorder(Rectangle.NO_BORDER);
        cValor.setBackgroundColor(fundo);

        table.addCell(cLabel);
        table.addCell(cValor);
    }

    private PdfPTable blocoTotalGeral(String label, String valor, Font fonte, BaseColor cor) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        Phrase frase = new Phrase();
        frase.add(new Chunk(label + "  ", new Font(Font.FontFamily.HELVETICA, 7.5f, Font.NORMAL, BaseColor.WHITE)));
        frase.add(new Chunk(valor, new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE)));
        PdfPCell cell = new PdfPCell(frase);
        cell.setPadding(5f);
        cell.setBackgroundColor(cor);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        return table;
    }

    private PdfPTable blocoAviso(String titulo, String texto, Font tituloFont, Font textoFont,
                                 BaseColor fundo, BaseColor borda) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        Paragraph p = new Paragraph();
        p.setLeading(8.5f);
        p.add(new Chunk(titulo.toUpperCase() + "  ", tituloFont));
        p.add(new Chunk(texto, textoFont));
        PdfPCell cell = new PdfPCell(p);
        cell.setPadding(3.5f);
        cell.setBackgroundColor(fundo);
        cell.setBorderColor(borda);
        cell.setBorderWidth(0.6f);
        table.addCell(cell);
        return table;
    }

    private PdfPTable gradeTextosOrcamento(String incluso, String naoIncluso, String complicacoes,
                                          String observacoes, Font tituloFont, Font textoFont,
                                          Font textoNegrito, BaseColor borda) throws DocumentException {
        PdfPTable grade = new PdfPTable(2);
        grade.setWidthPercentage(100);
        grade.setWidths(new float[]{1f, 1f});
        grade.setSpacingBefore(1f);
        grade.setSpacingAfter(2f);

        grade.addCell(celulaTextoGrade("Incluso", incluso, tituloFont, textoFont, borda));
        grade.addCell(celulaTextoGrade("Não incluso", naoIncluso, tituloFont, textoFont, borda));
        grade.addCell(celulaTextoGrade("Complicações e intercorrências", complicacoes, tituloFont, textoFont, borda));
        grade.addCell(celulaObservacoes("Observações", observacoes, tituloFont, textoFont, textoNegrito, borda));
        return grade;
    }

    private PdfPCell celulaTextoGrade(String titulo, String texto, Font tituloFont, Font textoFont, BaseColor borda) {
        Paragraph tituloP = new Paragraph(titulo.toUpperCase(), tituloFont);
        tituloP.setLeading(8.2f);
        tituloP.setAlignment(Element.ALIGN_LEFT);
        tituloP.setSpacingAfter(1.5f);

        Paragraph corpo = new Paragraph();
        corpo.setLeading(8.2f);
        corpo.setAlignment(Element.ALIGN_JUSTIFIED);
        if (texto != null && !texto.isBlank()) {
            corpo.add(new Chunk(texto.trim().replace("\n", " "), textoFont));
        } else {
            corpo.add(new Chunk("—", textoFont));
        }

        PdfPCell cell = new PdfPCell();
        cell.addElement(tituloP);
        cell.addElement(corpo);
        cell.setPadding(4f);
        cell.setBorderColor(borda);
        cell.setBorderWidth(0.5f);
        cell.setBorderWidthLeft(2f);
        cell.setBorderColorLeft(new BaseColor(30, 60, 114));
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        return cell;
    }

    /** Observações: horário de visitas em negrito e em linha própria. */
    private PdfPCell celulaObservacoes(String titulo, String texto, Font tituloFont, Font textoFont,
                                       Font textoNegrito, BaseColor borda) {
        Paragraph p = new Paragraph();
        p.setLeading(8.2f);
        p.add(new Chunk(titulo.toUpperCase() + "\n", tituloFont));

        if (texto != null && !texto.isBlank()) {
            String[] linhas = texto.trim().replace("\r\n", "\n").split("\n");
            for (int i = 0; i < linhas.length; i++) {
                String linha = linhas[i].trim();
                if (linha.isEmpty()) {
                    continue;
                }
                boolean horario = linha.toUpperCase().contains("HORÁRIO")
                        || linha.toUpperCase().contains("HORARIO")
                        || linha.toUpperCase().contains("VISITA");
                // Se veio tudo numa linha, separa o trecho de horário
                if (!horario && i == 0 && linhas.length == 1) {
                    int idx = indexOfIgnoreCase(linha, "HORÁRIO");
                    if (idx < 0) {
                        idx = indexOfIgnoreCase(linha, "HORARIO");
                    }
                    if (idx > 0) {
                        String antes = linha.substring(0, idx).trim().replaceAll("[.\\s]+$", "");
                        String depois = linha.substring(idx).trim();
                        if (!antes.isEmpty()) {
                            p.add(new Chunk(antes + "\n", textoFont));
                        }
                        p.add(new Chunk(depois, textoNegrito));
                        continue;
                    }
                }
                Font fonteLinha = horario ? textoNegrito : textoFont;
                p.add(new Chunk(linha + (i < linhas.length - 1 ? "\n" : ""), fonteLinha));
            }
        } else {
            p.add(new Chunk("—", textoFont));
        }

        PdfPCell cell = new PdfPCell(p);
        cell.setPadding(4f);
        cell.setBorderColor(borda);
        cell.setBorderWidth(0.5f);
        cell.setBorderWidthLeft(2f);
        cell.setBorderColorLeft(new BaseColor(30, 60, 114));
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        return cell;
    }

    private int indexOfIgnoreCase(String texto, String busca) {
        return texto.toUpperCase().indexOf(busca.toUpperCase());
    }

    private PdfPTable blocoTextoModerno(String titulo, String texto, Font tituloFont, Font textoFont, BaseColor borda) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2f);
        table.setSpacingAfter(0f);
        Paragraph p = new Paragraph();
        p.setLeading(9f);
        p.add(new Chunk(titulo.toUpperCase() + "  ", tituloFont));
        if (texto != null && !texto.isBlank()) {
            p.add(new Chunk(texto.trim().replace("\n", " "), textoFont));
        } else {
            p.add(new Chunk("—", textoFont));
        }
        PdfPCell cell = new PdfPCell(p);
        cell.setPadding(4f);
        cell.setBorderColor(borda);
        cell.setBorderWidth(0.5f);
        cell.setBorderWidthLeft(2.5f);
        cell.setBorderColorLeft(new BaseColor(30, 60, 114));
        table.addCell(cell);
        return table;
    }

    private Paragraph paragrafoSimples(String texto, Font fonte) {
        Paragraph p = new Paragraph(texto, fonte);
        p.setLeading(8f);
        p.setSpacingBefore(0f);
        return p;
    }

    private PdfPTable montarAssinaturaModerna(Orcamento orcamento, Font negrito, Font normal, Font pequeno,
                                              BaseColor borda, BaseColor fundo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = orcamento.getCreatedAt() != null ? orcamento.getCreatedAt().format(formatter) : "";

        Paragraph p = new Paragraph();
        p.setLeading(8.5f);
        p.add(new Chunk("Válido por 30 dias.  ", negrito));
        p.add(new Chunk("Responsável: ", negrito));
        p.add(new Chunk("TATIANA DOS SANTOS   ", normal));
        p.add(new Chunk("Data: " + dataFormatada, normal));

        if (orcamento.getStatus() == 2 && orcamento.getAprovadoEm() != null) {
            p.add(new Chunk("   |   Aprovado em: " + orcamento.getAprovadoEm().format(formatter), normal));
        }
        if (orcamento.getChave() != null && !orcamento.getChave().isBlank()) {
            p.add(new Chunk("   |   Código: " + orcamento.getChave(), pequeno));
        }

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2f);
        PdfPCell cell = new PdfPCell(p);
        cell.setPadding(4f);
        cell.setBackgroundColor(fundo);
        cell.setBorderColor(borda);
        cell.setBorderWidth(0.6f);
        table.addCell(cell);
        return table;
    }

    private byte[] gerarOrcamentoPdfLegado(Orcamento orcamento) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 100, 30);

        try {
            String textoDoBanco = orcamento.getDescricao() != null ? orcamento.getDescricao() : "";

            List<String> palavrasChave = new ArrayList<>(Arrays.asList(
                    "PROCEDIMENTO CIRÚRGICO:", "MÉDICO(A) RESPONSÁVEL:", "CIRURGIÃO:", "AUXILIAR:", "PEDIATRA:", "ANESTESISTA:", "INSTRUMENTADOR:",
                    "TOTAL EQUIPE MÉDICA: ", "OBS:", "DESPESAS HOSPITALARES", "MATERIAIS ESPECIAIS", "ANATOMOPATOLÓGICO",
                    "TOTAL:", "TOTAL GERAL DO PROCEDIMENTO:", "INCLUSO:", "NÃO INCLUSO: ", "OBSERVAÇÕES:",
                    "COMPLICAÇÕES E INTERCORRÊNCIAS:", "ORÇAMENTO É VÁLIDO POR 30 DIAS."));

            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            writer.setPageEvent(new HeaderFooterPageEvent("ORÇAMENTO MÉDICO HOSPITALAR", "src/main/resources/static/logohcf.png"));
            document.open();
            document.add(new Paragraph("\n"));

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font fonteNormal = new Font(Font.FontFamily.HELVETICA, 11);
            Font fonteNegrito = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font fonteTotalGeral = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fonteMenor = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

            document.add(montarTabelaPaciente(orcamento, headerFont));

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setSplitLate(false);

            Paragraph paragrafoComNegrito = new Paragraph();
            paragrafoComNegrito.setLeading(12f);

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
                        paragrafoComNegrito.add(new Chunk(palavraChave, fonteNegrito));
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

            PdfPCell cellTextoPrincipal = new PdfPCell();
            cellTextoPrincipal.addElement(paragrafoComNegrito);
            cellTextoPrincipal.setBorder(Rectangle.BOX);
            table.addCell(cellTextoPrincipal);
            table.addCell(montarCelulaAssinatura(orcamento, fonteNegrito, fonteNormal, fonteMenor));
            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return byteArrayOutputStream.toByteArray();
    }

    private PdfPTable montarTabelaPaciente(Orcamento orcamento, Font headerFont) throws DocumentException {
        PdfPTable tableDadosCliente = new PdfPTable(2);
        tableDadosCliente.setWidths(new float[]{1.5f, 1f});
        tableDadosCliente.setWidthPercentage(100);

        tableDadosCliente.addCell(new PdfPCell(new Phrase(String.format("NOME PACIENTE:  %s", safe(orcamento.getNome()).toUpperCase()), headerFont)));
        tableDadosCliente.addCell(new PdfPCell(new Phrase(String.format("SEXO:  %s", safe(orcamento.getSexo()).toUpperCase()), headerFont)));
        tableDadosCliente.addCell(new PdfPCell(new Phrase(String.format("E-MAIL:  %s", safe(orcamento.getEmail()).toUpperCase()), headerFont)));
        tableDadosCliente.addCell(new PdfPCell(new Phrase(String.format("CELULAR:  %s", safe(orcamento.getCelular()).toUpperCase()), headerFont)));
        return tableDadosCliente;
    }

    private PdfPCell montarCelulaAssinatura(Orcamento orcamento, Font fonteNegrito, Font fonteNormal, Font fonteMenor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = orcamento.getCreatedAt() != null ? orcamento.getCreatedAt().format(formatter) : "";

        Phrase frase = new Phrase();
        frase.add(new Chunk("RESPONSÁVEL PELO ORÇAMENTO: ", fonteNegrito));
        frase.add(new Chunk("TATIANA DOS SANTOS \n", fonteNormal));
        frase.add(new Chunk("DATA: " + dataFormatada + "\n", fonteNegrito));

        if (orcamento.getStatus() == 2 && orcamento.getAprovadoEm() != null) {
            String dataAprovadoFormatada = orcamento.getAprovadoEm().format(formatter);
            frase.add(new Chunk("\nAPROVADO EM: ", fonteNegrito));
            frase.add(new Chunk(dataAprovadoFormatada + "\n", fonteNormal));
        }
        if (orcamento.getChave() != null) {
            frase.add(new Chunk(orcamento.getChave(), fonteMenor));
        }

        PdfPCell cellAssinatura = new PdfPCell(frase);
        cellAssinatura.setPadding(12f);
        return cellAssinatura;
    }

    private void adicionarLinhaRotulo(Paragraph paragrafo, String rotulo, String valor, Font fonteNegrito, Font fonteNormal) {
        paragrafo.add(new Chunk(rotulo, fonteNegrito));
        paragrafo.add(new Chunk(" " + valor, fonteNormal));
        paragrafo.add(Chunk.NEWLINE);
    }

    private void adicionarBlocoTexto(Paragraph paragrafo, String rotulo, String texto, Font fonteNegrito, Font fonteNormal) {
        paragrafo.add(new Chunk(rotulo, fonteNegrito));
        paragrafo.add(Chunk.NEWLINE);
        if (texto != null && !texto.isBlank()) {
            for (String linha : texto.split("\n")) {
                paragrafo.add(new Chunk(linha, fonteNormal));
                paragrafo.add(Chunk.NEWLINE);
            }
        }
    }

    private List<OrcamentoItem> filtrarSecao(List<OrcamentoItem> itens, SecaoOrcamentoItem secao) {
        return itens.stream()
                .filter(i -> i.getSecao() == secao)
                .sorted(Comparator.comparingInt(OrcamentoItem::getOrdem).thenComparing(OrcamentoItem::getId, Comparator.nullsLast(Long::compareTo)))
                .toList();
    }

    private BigDecimal valorItem(OrcamentoItem item) {
        return item.getValor() != null ? item.getValor() : BigDecimal.ZERO;
    }

    private String formatarMoeda(DecimalFormat df, BigDecimal valor) {
        return df.format(valor);
    }

    private String safe(String value) {
        return value != null ? value : "";
    }


    public byte[] gerarPdf(Long pagamentoId) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 24, 24, 88, 24);

        try {
            DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
            List<RelatorioPagamentoDTO> pagamentos = pagamentoRepository.findByPagamentoId(pagamentoId);
            RelatorioPagamentoDTO primeiro = pagamentos.getFirst();
            long idRepasse = primeiro.getRepasseId();
            this.emailMedico = primeiro.getEmail();

            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            writer.setPageEvent(new HeaderFooterPageEvent(
                    "DEMONSTRATIVO DE PAGAMENTO",
                    "src/main/resources/static/logohcf.png",
                    true));
            document.open();

            BaseColor azulEscuro = new BaseColor(30, 60, 114);
            BaseColor azulSuave = new BaseColor(241, 245, 249);
            BaseColor cinzaBorda = new BaseColor(226, 232, 240);
            BaseColor cinzaTexto = new BaseColor(51, 65, 85);
            BaseColor destaqueTotal = new BaseColor(226, 232, 240);
            BaseColor zebra = new BaseColor(248, 250, 252);

            Font tituloSecao = new Font(Font.FontFamily.HELVETICA, 7.5f, Font.BOLD, azulEscuro);
            Font rotulo = new Font(Font.FontFamily.HELVETICA, 6.5f, Font.NORMAL, new BaseColor(100, 116, 139));
            Font valorCampo = new Font(Font.FontFamily.HELVETICA, 7.5f, Font.BOLD, cinzaTexto);
            Font textoNormal = new Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, cinzaTexto);
            Font textoNegrito = new Font(Font.FontFamily.HELVETICA, 7f, Font.BOLD, cinzaTexto);
            Font textoCabecalho = new Font(Font.FontFamily.HELVETICA, 7f, Font.BOLD, azulEscuro);
            Font textoTotal = new Font(Font.FontFamily.HELVETICA, 7.5f, Font.BOLD, azulEscuro);

            document.add(montarCabecalhoRepasse(primeiro, rotulo, valorCampo, azulSuave, cinzaBorda));
            document.add(tituloSecaoBarra("Procedimentos", tituloSecao, azulSuave));

            PdfPTable table = criarTabelaProcedimentos();
            adicionarCabecalhoProcedimentos(table, textoCabecalho, azulSuave, cinzaBorda);

            double total = 0;
            int rowIndex = 0;

            for (RelatorioPagamentoDTO p : pagamentos) {
                if (writer.getVerticalPosition(true) < 110) {
                    document.add(table);
                    document.newPage();
                    document.add(montarCabecalhoRepasse(primeiro, rotulo, valorCampo, azulSuave, cinzaBorda));
                    document.add(tituloSecaoBarra("Procedimentos (continuação)", tituloSecao, azulSuave));
                    table = criarTabelaProcedimentos();
                    adicionarCabecalhoProcedimentos(table, textoCabecalho, azulSuave, cinzaBorda);
                    rowIndex = 0;
                }

                BaseColor fundoLinha = (rowIndex % 2 == 0) ? BaseColor.WHITE : zebra;
                adicionarLinhaProcedimento(table, p, df, textoNormal, textoNegrito, cinzaBorda, fundoLinha);
                total += p.getValorProcedimento();
                rowIndex++;
            }

            long valorArredondado = Math.round(total);
            adicionarLinhaTotalProcedimentos(table, "Total bruto", "R$ " + df.format(valorArredondado),
                    textoTotal, destaqueTotal);
            document.add(table);

            List<DespesaMedico> despesas = despesaMedicoRepository.findByRepasseId(idRepasse);
            if (despesas != null && !despesas.isEmpty()) {
                gerarResumoDespesasModerno(document, despesas, df, valorArredondado,
                        tituloSecao, textoNormal, textoNegrito, textoTotal,
                        azulEscuro, azulSuave, cinzaBorda, destaqueTotal);
            } else {
                PdfPTable totalBox = blocoTotalGeral("Total", "R$ " + df.format(valorArredondado), textoTotal, azulEscuro);
                totalBox.setSpacingBefore(6f);
                document.add(totalBox);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    private PdfPTable montarCabecalhoRepasse(RelatorioPagamentoDTO pagamento, Font rotulo, Font valor,
                                             BaseColor fundo, BaseColor borda) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 1.4f, 1.2f});
        table.setSpacingBefore(0f);
        table.setSpacingAfter(4f);

        String banco = pagamento.getBancoPagamento() != null ? pagamento.getBancoPagamento() : "";
        String dataDeposito = "";
        if (pagamento.getDataDeposito() != null) {
            dataDeposito = UtilStrings.formartaDataBr(pagamento.getDataDeposito().toString());
        }

        table.addCell(celulaCampoModerno("Médico", safe(pagamento.getMedico()), rotulo, valor, borda, fundo));
        table.addCell(celulaCampoModerno("Banco", banco, rotulo, valor, borda, fundo));
        table.addCell(celulaCampoModerno("Data depósito", dataDeposito, rotulo, valor, borda, fundo));
        return table;
    }

    private PdfPTable criarTabelaProcedimentos() throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.85f, 2.2f, 1.3f, 2.2f, 0.9f});
        table.setSpacingBefore(0f);
        table.setSpacingAfter(2f);
        table.setHeaderRows(1);
        table.setSplitLate(false);
        return table;
    }

    private void adicionarCabecalhoProcedimentos(PdfPTable table, Font fonte, BaseColor fundo, BaseColor borda) {
        String[] titulos = {"Data", "Paciente", "Convênio", "Procedimento", "Valor"};
        for (int i = 0; i < titulos.length; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(titulos[i].toUpperCase(), fonte));
            cell.setPadding(4f);
            cell.setBackgroundColor(fundo);
            cell.setBorderColor(borda);
            cell.setBorderWidth(0.5f);
            if (i == titulos.length - 1) {
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            }
            table.addCell(cell);
        }
    }

    private void adicionarLinhaProcedimento(PdfPTable table, RelatorioPagamentoDTO p, DecimalFormat df,
                                            Font texto, Font textoValor, BaseColor borda, BaseColor fundo) {
        String data = p.getDataProcedimento() != null
                ? UtilStrings.formartaDataBr(p.getDataProcedimento().toString())
                : "—";

        table.addCell(celulaTextoTabela(data, texto, borda, fundo, Element.ALIGN_LEFT));
        table.addCell(celulaTextoTabela(safe(p.getPaciente()).toUpperCase(), texto, borda, fundo, Element.ALIGN_LEFT));
        table.addCell(celulaTextoTabela(safe(p.getConvenio()).toUpperCase(), texto, borda, fundo, Element.ALIGN_LEFT));
        table.addCell(celulaTextoTabela(safe(p.getProcedimento()).toUpperCase(), texto, borda, fundo, Element.ALIGN_LEFT));
        table.addCell(celulaTextoTabela(df.format(p.getValorProcedimento()), textoValor, borda, fundo, Element.ALIGN_RIGHT));
    }

    private PdfPCell celulaTextoTabela(String texto, Font fonte, BaseColor borda, BaseColor fundo, int alinhamento) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", fonte));
        cell.setPadding(3.5f);
        cell.setBorderColor(borda);
        cell.setBorderWidth(0.5f);
        cell.setBackgroundColor(fundo);
        cell.setHorizontalAlignment(alinhamento);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private void adicionarLinhaTotalProcedimentos(PdfPTable table, String label, String valor,
                                                  Font fonte, BaseColor fundo) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, fonte));
        cLabel.setColspan(4);
        cLabel.setPadding(5f);
        cLabel.setBorder(Rectangle.NO_BORDER);
        cLabel.setBackgroundColor(fundo);
        cLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell cValor = new PdfPCell(new Phrase(valor, fonte));
        cValor.setPadding(5f);
        cValor.setBorder(Rectangle.NO_BORDER);
        cValor.setBackgroundColor(fundo);
        cValor.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(cLabel);
        table.addCell(cValor);
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
            """, data);
    }

    private void gerarResumoDespesasModerno(Document document,
                                            List<DespesaMedico> despesas,
                                            DecimalFormat df,
                                            double totalCredito,
                                            Font tituloSecao,
                                            Font textoNormal,
                                            Font textoNegrito,
                                            Font textoTotal,
                                            BaseColor azulEscuro,
                                            BaseColor azulSuave,
                                            BaseColor cinzaBorda,
                                            BaseColor destaqueTotal) throws DocumentException {

        document.add(tituloSecaoBarra("Despesas", tituloSecao, azulSuave));

        PdfPTable tabelaDespesas = new PdfPTable(2);
        tabelaDespesas.setWidthPercentage(100);
        tabelaDespesas.setWidths(new float[]{3.2f, 1f});
        tabelaDespesas.setSpacingAfter(4f);

        double totalDespesas = 0;
        for (DespesaMedico d : despesas) {
            totalDespesas += d.getValor();
            adicionarLinhaItem(
                    tabelaDespesas,
                    safe(d.getDescricao()).toUpperCase(),
                    "R$ " + df.format(d.getValor()),
                    textoNormal,
                    textoNegrito,
                    cinzaBorda);
        }
        adicionarLinhaTotal(tabelaDespesas, "Total despesas", "R$ " + df.format(totalDespesas),
                textoTotal, destaqueTotal);
        document.add(tabelaDespesas);

        double totalFinal = totalCredito - totalDespesas;

        PdfPTable resumo = new PdfPTable(2);
        resumo.setWidthPercentage(100);
        resumo.setWidths(new float[]{3.2f, 1f});
        resumo.setSpacingBefore(2f);

        PdfPCell[] bruto = celulaResumo("Valor total bruto", "R$ " + df.format(totalCredito),
                textoNormal, textoNegrito, cinzaBorda, BaseColor.WHITE);
        resumo.addCell(bruto[0]);
        resumo.addCell(bruto[1]);

        PdfPCell[] desp = celulaResumo("Total despesas", "R$ " + df.format(totalDespesas),
                textoNormal, textoNegrito, cinzaBorda, azulSuave);
        resumo.addCell(desp[0]);
        resumo.addCell(desp[1]);

        PdfPCell labelLiquido = new PdfPCell(new Phrase("Valor a receber",
                new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE)));
        labelLiquido.setPadding(6f);
        labelLiquido.setBorder(Rectangle.NO_BORDER);
        labelLiquido.setBackgroundColor(azulEscuro);

        PdfPCell valorLiquido = new PdfPCell(new Phrase("R$ " + df.format(totalFinal),
                new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE)));
        valorLiquido.setPadding(6f);
        valorLiquido.setBorder(Rectangle.NO_BORDER);
        valorLiquido.setBackgroundColor(azulEscuro);
        valorLiquido.setHorizontalAlignment(Element.ALIGN_RIGHT);

        resumo.addCell(labelLiquido);
        resumo.addCell(valorLiquido);
        document.add(resumo);
    }

    private PdfPCell[] celulaResumo(String label, String valor, Font fonteLabel, Font fonteValor,
                                    BaseColor borda, BaseColor fundo) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, fonteLabel));
        cLabel.setPadding(4f);
        cLabel.setBorderColor(borda);
        cLabel.setBorderWidth(0.5f);
        cLabel.setBackgroundColor(fundo);

        PdfPCell cValor = new PdfPCell(new Phrase(valor, fonteValor));
        cValor.setPadding(4f);
        cValor.setBorderColor(borda);
        cValor.setBorderWidth(0.5f);
        cValor.setBackgroundColor(fundo);
        cValor.setHorizontalAlignment(Element.ALIGN_RIGHT);

        return new PdfPCell[]{cLabel, cValor};
    }
}

