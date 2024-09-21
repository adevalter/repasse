package br.com.adeweb.repasse.data.models;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaDTO {
    private Long id;
    private String razao;
    private String fantasia;
    private String email;
    private String celular;
    private int status;
}
