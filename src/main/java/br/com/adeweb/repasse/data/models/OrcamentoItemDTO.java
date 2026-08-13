package br.com.adeweb.repasse.data.models;

import br.com.adeweb.repasse.domain.enums.SecaoOrcamentoItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrcamentoItemDTO {
    private Long id;
    private SecaoOrcamentoItem secao;
    private String descricao;
    private BigDecimal valor;
    private int ordem;
}
