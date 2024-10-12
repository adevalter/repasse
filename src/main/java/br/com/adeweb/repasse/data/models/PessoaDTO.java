package br.com.adeweb.repasse.data.models;


import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
    private Long id;
    private String nome;
    private String documento;
    private String obs;
    private int status;

    UserResumoDTO user;
    EmpresaResumoDTO empresa;
    ConvenioResumoDTO convenio;
    TipoPessoaResumoDTO tipoPessoa;
}
