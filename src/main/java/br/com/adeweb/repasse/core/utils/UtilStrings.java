package br.com.adeweb.repasse.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilStrings {
  public  static String formartaDataBr(String dataFormatar){
        LocalDateTime data = LocalDateTime.parse(dataFormatar);

        // Formatar para o formato desejado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return data.format(formatter);
    }
}
