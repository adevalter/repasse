package br.com.adeweb.repasse.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String nome;
    private String documento;
    private String obs;
    private int status;
    @ManyToOne
    @JsonIgnoreProperties("pessoa")
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JsonIgnoreProperties("pessoa")
    @JoinColumn(name = "empresa_id")
    Empresa empresa;
    @ManyToOne
    @JsonIgnoreProperties("pessoa")
    @JoinColumn(name = "convenio_id")
    Convenio convenio;

    @ManyToOne
    @JsonIgnoreProperties("pessoa")
    @JoinColumn(name = "tipoPessoa_id")
    TipoPessoa tipoPessoa;


    @CreationTimestamp
    @Column(updatable = false, name="created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAd;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name="updated_at", columnDefinition = "dateTime")
    private LocalDateTime updatedAt;

}
