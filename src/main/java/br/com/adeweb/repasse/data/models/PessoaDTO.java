package br.com.adeweb.repasse.data.models;


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

   private UserResumoDTO user;
   private EmpresaResumoDTO empresa;
   private ConvenioResumoDTO convenio;
   private TipoPessoaResumoDTO tipoPessoa;
}
