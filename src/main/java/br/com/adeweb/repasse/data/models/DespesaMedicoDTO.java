package br.com.adeweb.repasse.data.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DespesaMedicoDTO {
    private long id;
    private String descricao;
    private double valor;
    private int status;
    private LocalDateTime createdAd;

    private RepasseResumoDTO repasse;
    private UserResumoDTO user;
}
