package br.com.adeweb.repasse.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilStrings {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public  static String formartaDataBr(String dataFormatar){
        LocalDateTime data = LocalDateTime.parse(dataFormatar);

        // Formatar para o formato desejado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return data.format(formatter);
    }

    // Função para gerar o token
    public static String gerarToken(long id) {
        // Obtem data e hora
        LocalDateTime agora = LocalDateTime.now();
        String dataStr = agora.format(DateTimeFormatter.ofPattern("yyMMdd")); // 0501
        String horaStr = agora.format(DateTimeFormatter.ofPattern("HHmmss"));   // 2105

        // Soma tudo
        long data = Long.parseLong(dataStr);
        long hora = Long.parseLong(horaStr);
        long soma = data + hora + id;

        // Converte para Base62
        String base62 = toBase62(soma);

        // Limita a 8 caracteres
        if (base62.length() > 8) {
            base62 = base62.substring(base62.length() - 8); // pega os últimos 8 caracteres
        }

        // Retorna o token
        return "HCF" + base62;
    }

    // Conversor para Base62
    private static String toBase62(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % 62);
            sb.insert(0, BASE62.charAt(remainder));
            value /= 62;
        }
        return sb.toString();
    }

    // Conversor de Base62 para número
    private static long fromBase62(String base62) {
        long result = 0;
        for (int i = 0; i < base62.length(); i++) {
            result = result * 62 + BASE62.indexOf(base62.charAt(i));
        }
        return result;
    }

    // Função para recuperar o ID original
    public static long recuperarId(String token) {
        // Remove o prefixo "TK"
        String base62 = token.substring(2);

        // Reverte de Base62 para número
        long valorOriginal = fromBase62(base62);

        // Aqui você precisaria saber a data e hora usadas para encontrar o id
        // Exemplo simples com data/hora conhecidas:
        LocalDateTime agora = LocalDateTime.now();
        long data = Long.parseLong(agora.format(DateTimeFormatter.ofPattern("MMdd")));  // Exemplo 0501
        long hora = Long.parseLong(agora.format(DateTimeFormatter.ofPattern("HHmm")));   // Exemplo 2105

        // Calcula o id: valorOriginal = data + hora + id
        long id = valorOriginal - data - hora;
        return id;
    }
}
