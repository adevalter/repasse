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
@NoArgsConstructor
@AllArgsConstructor
public class Procedimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    private String descricao;
    private int status;

    @CreationTimestamp
    @Column(updatable = false, name="created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAd;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name="updated_at", columnDefinition = "dateTime")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonIgnoreProperties("procedimentos")
    private User user;

    @ManyToOne
    @JoinColumn(name= "empresa_id",nullable = false)
    @JsonIgnoreProperties("procedimentos")
    private Empresa empresa;
}
