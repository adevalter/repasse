package br.com.adeweb.repasse.data.models;

import br.com.adeweb.repasse.domain.entities.Empresa;
import br.com.adeweb.repasse.domain.enums.StatusUsers;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UsersDTO {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String forget;
    private int status;
    private Empresa empresa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
