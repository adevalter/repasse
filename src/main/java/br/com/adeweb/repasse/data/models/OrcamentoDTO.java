package br.com.adeweb.repasse.data.models;

import br.com.adeweb.repasse.domain.enums.TipoOrcamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrcamentoDTO {
    private long id;
    private String nome;
    private String sexo;
    private String email;
    private String celular;
    private String chave;
    private TipoOrcamento tipo;
    private String procedimentoCirurgico;
    private String equipeMedica;
    private String obsPagamento;
    private String incluso;
    private String naoIncluso;
    private String complicacoes;
    private String observacoes;
    /** Legado — preferir campos estruturados + itens. */
    private String descricao;
    private int status;
    private LocalDateTime aprovadoEm;
    private LocalDateTime createdAt;
    private UserResumoDTO user;
    private UserResumoDTO aprovado;
    private List<OrcamentoItemDTO> itens = new ArrayList<>();
    private BigDecimal totalEquipe;
    private BigDecimal totalHospitalar;
    private BigDecimal totalMaterial;
    private BigDecimal totalGeral;
}
