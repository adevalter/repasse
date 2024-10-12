package br.com.adeweb.repasse.data.models;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConvenioDTO {
    private Long id;
    private String descricao;
    private int status;
    private LocalDateTime createdAd;
    private LocalDateTime updatedAt;

    private UserResumoDTO user;
    private EmpresaResumoDTO empresa;
}
