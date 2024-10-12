package br.com.adeweb.repasse.data.models;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String forget;
    private int status;


    private EmpresaResumoDTO empresa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
