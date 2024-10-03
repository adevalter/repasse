package br.com.adeweb.repasse.data.models;

import br.com.adeweb.repasse.domain.entities.Empresa;
import br.com.adeweb.repasse.domain.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ProcedimentoDTO {
    private long id;
    private String descricao;
    private int status;
    private UserDTO user;
    private EmpresaDTO empresa;
}
