package br.com.adeweb.repasse.data.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RepasseDTO {
    private long id;
    private int status;
    private PessoaResumoDTO medico;
    private UserResumoDTO user;
    private EmpresaResumoDTO empresa;
    private List<RepasseItemResumoDTO> repasseItems;
    private LocalDateTime createdAt;
}
