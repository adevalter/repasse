package br.com.adeweb.repasse.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Repasse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    private int status;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Pessoa medico;
    @ManyToOne
    @JoinColumn(name="paciente_id")
    private Pessoa pacitente;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @CreationTimestamp
    @Column(updatable = false, name="created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAd;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name="updated_at", columnDefinition = "dateTime")
    private LocalDateTime updatedAt;



}
