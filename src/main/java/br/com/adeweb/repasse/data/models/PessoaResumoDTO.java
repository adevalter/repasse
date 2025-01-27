package br.com.adeweb.repasse.data.models;

import lombok.Data;

@Data
public class PessoaResumoDTO {
    private Long id;
    private String nome;
    private String email;
    private ConvenioResumoDTO convenio;

}
