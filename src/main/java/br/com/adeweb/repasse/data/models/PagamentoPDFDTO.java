package br.com.adeweb.repasse.data.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoPDFDTO {
    private Long id;
    private Double total;
    private String bancoPagamento;
    private LocalDateTime dataDeposito;
    private int status;

   private UserResumoDTO user;

   private List<RepasseItemDTO> repasseItem;


}
