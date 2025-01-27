package br.com.adeweb.repasse.data.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepasseItemDTO {

    private Long id;
    private RepasseResumoDTO repasse;
    private PessoaResumoDTO paciente;
    private ProcedimentoResumoDTO procedimento;
    private UserResumoDTO user;
    private EmpresaResumoDTO empresa;
    private LocalDateTime dataProcedimento;
    private double valor;
    private int status;
}
