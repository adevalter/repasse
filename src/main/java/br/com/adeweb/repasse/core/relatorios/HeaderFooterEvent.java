package br.com.adeweb.repasse.core.relatorios;

import br.com.adeweb.repasse.core.utils.UtilStrings;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.time.LocalDateTime;


public class HeaderFooterEvent extends PdfPageEventHelper {
    String logoPath = "src/main/resources/static/logohcf.png";

    private Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);


    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        try {
            // Carregar a imagem
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(60, 60); // Ajustar o tamanho da imagem

            // Ajustar a posição da imagem (subtrai a altura para evitar o corte no topo)
            float imageX = document.left();
            float imageY = document.top() ; // Subtrair a altura escalada da imagem
            logo.setAbsolutePosition(imageX, imageY);

            cb.addImage(logo);

            // Texto do cabeçalho
//            Phrase headerText = new Phrase("Relatório Médico - Hospital ",
//                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
//            float headerTextX = document.left() + 100; // Ajustar para que fique após a imagem
//            float headerTextY = document.top() + 40; // Altura do texto no cabeçalho
//            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, headerText, headerTextX, headerTextY, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Cabeçalho
        Phrase header = new Phrase("Hospital das Clínicas Fernandópolis" , new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header, (document.left() + document.right()) / 2, document.top() + 60, 0);
        Phrase endereco = new Phrase("Av. Expedicionários Brasileiros, 1181, Centro. Fone (17) 3442-1844", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, endereco, (document.left() + document.right()) / 2, document.top() + 40, 0);
        Phrase cidade = new Phrase("Fernandópolis - SP. CEP 15600-000 - hospitaldasclinicas@terra.com.br", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, cidade, (document.left() + document.right()) / 2, document.top() + 20, 0);
        Phrase repasse = new Phrase("FORMULÁRIO DE REPASSE TERCEIRO" , new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, repasse, (document.left() + document.right()) / 2, document.top() -5  , 0);


        // Rodapé
        Phrase footer = new Phrase("Página " + writer.getPageNumber(), new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL));
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (document.left() + document.right()) / 2, document.bottom() - 10, 0);
    }
}
