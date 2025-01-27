package br.com.adeweb.repasse.data.models;


import br.com.adeweb.repasse.domain.entities.RepasseItem;
import br.com.adeweb.repasse.domain.repositories.RepasseItemRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
    private Long id;
    private String nome;
    private String documento;
    private String email;
    private int status;

   private UserResumoDTO user;
   private EmpresaResumoDTO empresa;
   private ConvenioResumoDTO convenio;
   private TipoPessoaResumoDTO tipoPessoa;

}
