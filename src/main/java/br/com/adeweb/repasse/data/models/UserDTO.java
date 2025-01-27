package br.com.adeweb.repasse.data.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String forget;
    private int permissao;
    private int status;


    private EmpresaResumoDTO empresa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
