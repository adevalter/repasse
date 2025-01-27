package br.com.adeweb.repasse.data.models;


import lombok.Data;

@Data
public class ProcedimentoDTO {
    private long id;
    private String descricao;
    private int status;
    private UserResumoDTO user;
    private EmpresaResumoDTO empresa;

}
