package br.com.adeweb.repasse.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name="orcamento")
@Data
@Entity
public class Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    private String nome;
    private String sexo;
    private String email;
    private String celular;
    private String chave;
    private String descricao;
    private int status;

    @ManyToOne
    @JoinColumn(name = "aprovado_id")
    User aprovado;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;


    @Column(name="aprovado_em", columnDefinition = "dateTime")
    private LocalDateTime aprovadoEm;

    @CreationTimestamp
    @Column(updatable = false, name="created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name="updated_at", columnDefinition = "dateTime")
    private LocalDateTime updatedAt;
}
