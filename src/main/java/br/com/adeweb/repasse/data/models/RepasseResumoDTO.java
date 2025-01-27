package br.com.adeweb.repasse.data.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepasseResumoDTO {
    private Long id;
    private int status;
    private PessoaResumoDTO medico;
    private LocalDateTime createdAt;
}
