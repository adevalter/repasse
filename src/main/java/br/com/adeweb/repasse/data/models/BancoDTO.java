package br.com.adeweb.repasse.data.models;

import lombok.Data;

@Data
public class BancoDTO {
    private Long id;
    private String descricao;
    private UserResumoDTO user;
    private EmpresaResumoDTO empresa;
}
