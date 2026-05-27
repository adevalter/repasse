package br.com.adeweb.repasse.data.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class OrcamentoDTO {
    private long id;
    private String nome;
    private String sexo;
    private String email;
    private String celular;
    private String chave;
    private String descricao;
    private int status;
    private LocalDateTime aprovadoEm;
    private LocalDateTime createdAt;
    private UserResumoDTO user;
    private UserResumoDTO aprovado;
}
