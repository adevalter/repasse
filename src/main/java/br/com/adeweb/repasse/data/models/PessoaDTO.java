package br.com.adeweb.repasse.data.models;


import com.fasterxml.jackson.annotation.JsonInclude;
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
    UserDTO user;
    EmpresaDTO empresa;
    ConvenioDTO convenio;
    TipoPessoaDTO tipoPessoa;


}
