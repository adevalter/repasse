package br.com.adeweb.repasse.data.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaDTO {
    private Long id;
    private String razao;
    private String fantasia;
    private String email;
    private String document;
    private String celular;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private int status;
}
