package br.com.adeweb.repasse.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    
    
    private String razao;

    private String fantasia;

    private String email;

    private String document;
    
    private String celular;
    
    private int status;
    
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "dateTime")
    private LocalDateTime updateAt;







}
