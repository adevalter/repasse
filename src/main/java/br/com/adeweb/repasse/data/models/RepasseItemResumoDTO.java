package br.com.adeweb.repasse.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepasseItemResumoDTO {
    private Long id;
    private double valor;
}
