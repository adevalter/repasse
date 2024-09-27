package br.com.adeweb.repasse.data.models;

import br.com.adeweb.repasse.domain.entities.Empresa;
import br.com.adeweb.repasse.domain.entities.User;
import lombok.Data;

@Data
public class ProcedimentoDTO {
    private long id;
    private String descricao;
    private int status;
    private User user;
    private Empresa empresa;
}
