package br.com.adeweb.repasse.data.models;

import lombok.Data;

@Data
public class RepasseDTO {
    private long id;
    private int status;
    private PessoaResumoDTO medico;
    private PessoaResumoDTO pacitente;
    private UserResumoDTO user;
    private EmpresaResumoDTO empresa;
}
