package br.com.adeweb.repasse.domain.entities;

import java.time.LocalDateTime;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @OneToMany(mappedBy = "empresa")
    @JsonIgnoreProperties("empresa")
    private List<User> user;

    @OneToMany(mappedBy = "empresa")

    private List<Pessoa> pessoa;

    @OneToMany(mappedBy = "empresa")
    @JsonIgnoreProperties("empresa")
    private List<TipoPessoa> tipoPessoa;

    @OneToMany(mappedBy = "empresa")
    private List<Procedimento> procedimento;

    @OneToMany(mappedBy = "empresa")
    private List<Convenio> convenio;



}
