package br.com.adeweb.repasse.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConvenioDTO {
    private Long id;
    private String descricao;
    private int status;
    private LocalDateTime createdAd;
    private LocalDateTime updatedAt;
    private UserDTO user;
    private EmpresaDTO empresa;
}
