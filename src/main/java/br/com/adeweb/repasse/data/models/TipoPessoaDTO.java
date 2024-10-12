package br.com.adeweb.repasse.data.models;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;



@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoPessoaDTO {
    private Long id;
    private String descricao;
    private int status;

    private UserResumoDTO user;
    private EmpresaResumoDTO empresa;



}
