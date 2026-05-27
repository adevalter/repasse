package br.com.adeweb.repasse.data.models;

import br.com.adeweb.repasse.domain.entities.Procedimento;
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
