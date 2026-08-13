package br.com.adeweb.repasse.core.relatorios;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private final String titulo;
    private final String logoPath;
    private final boolean compact;

    public HeaderFooterPageEvent(String titulo, String logoPath) {
        this(titulo, logoPath, false);
    }

    public HeaderFooterPageEvent(String titulo, String logoPath, boolean compact) {
        this.titulo = titulo;
        this.logoPath = logoPath;
        this.compact = compact;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        // Margem do Document reserva o espaço; cabeçalho é desenhado no onEndPage
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();

        float pageTop = PageSize.A4.getHeight();
        float center = (document.right() + document.left()) / 2f;
        float leftMargin = document.left();

        // Reserva ~12mm no topo para área não imprimível da impressora
        float topSafe = 34f;

        try {
            Image logo = Image.getInstance(logoPath);
            if (compact) {
                logo.scaleToFit(42, 42);
                logo.setAbsolutePosition(leftMargin, pageTop - topSafe - 48);
            } else {
                logo.scaleToFit(60, 60);
                logo.setAbsolutePosition(leftMargin, pageTop - topSafe - 55);
            }
            cb.addImage(logo);
        } catch (Exception ignored) {
        }

        if (compact) {
            Font nome = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font info = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font rodape = new Font(Font.FontFamily.HELVETICA, 8);

            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase("Hospital das Clínicas de Fernandópolis", nome),
                    center, pageTop - topSafe - 2, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase("Av. Expedicionários Brasileiros, 1181 - Centro - Fone (17) 3442-1844", info),
                    center, pageTop - topSafe - 14, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase("Fernandópolis - SP - CEP 15600-001 - hospitaldasclinicas@terra.com.br", info),
                    center, pageTop - topSafe - 24, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase(titulo, tituloFont),
                    center, pageTop - topSafe - 40, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase("Página " + writer.getPageNumber(), rodape),
                    center, document.bottom() - 8, 0);
            return;
        }

        Font f16b = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font f12b = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font f14b = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font f10 = new Font(Font.FontFamily.HELVETICA, 10);

        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Hospital das Clínicas de Fernandópolis", f16b),
                center, pageTop - topSafe - 4, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Av. Expedicionários Brasileiros, 1181 - Centro - Fone (17) 3442-1844", f12b),
                center, pageTop - topSafe - 20, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Fernandópolis - SP - CEP 15600-001 - hospitaldasclinicas@terra.com.br", f12b),
                center, pageTop - topSafe - 36, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase(titulo, f14b),
                center, pageTop - topSafe - 58, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("Página " + writer.getPageNumber(), f10),
                center, document.bottom() - 10, 0);
    }
}
