package br.com.adeweb.repasse.core.relatorios;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private final String titulo;
    private final String logoPath;

    public HeaderFooterPageEvent(String titulo, String logoPath) {
        this.titulo = titulo;
        this.logoPath = logoPath;
    }

    private Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        // Não precisa adicionar espaçamento aqui, a margem do Document já garante o espaço
        // O cabeçalho é desenhado no topo absoluto da página no onEndPage
    }
    // -------------------------------------------------------

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();

        // Usa o topo absoluto da página (não document.top() que é afetado pela margem)
        float pageTop = PageSize.A4.getHeight();
        float center = (document.right() + document.left()) / 2f;
        float leftMargin = document.left();

        // LOGO - posicionado no topo absoluto da página
        try {
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(60, 60);
            logo.setAbsolutePosition(leftMargin, pageTop - 70);
            cb.addImage(logo);
        } catch (Exception ignored) {

        }

        Font f16b = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font f12b = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font f14b = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font f10 = new Font(Font.FontFamily.HELVETICA, 10);

        // Nome Hospital - posicionado a partir do topo absoluto
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Hospital das Clínicas de Fernandópolis", f16b), center, pageTop - 20,
                0);

        // Endereço
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Av. Expedicionários Brasileiros, 1181 - Centro - Fone (17) 3442-1844",
                        f12b),
                center, pageTop - 35, 0);

        // Cidade
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Fernandópolis - SP - CEP 15600-001 - hospitaldasclinicas@terra.com.br",
                        f12b),
                center, pageTop - 50, 0);

        // Título do formulário (o mais importante) - posicionado abaixo do cabeçalho
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase(titulo, f14b), center,
                pageTop - 75, 0);

        // Número da página (rodapé)
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Página " + writer.getPageNumber(), f10), center, document.bottom() - 10,
                0);
    }
}
