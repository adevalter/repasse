package br.com.adeweb.repasse.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
@Table(name="repasse_item")
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RepasseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(name="data_procedimento")
    private LocalDateTime dataProcedimento;
    private double valor;
    private int status;

    @ManyToOne
    @JoinColumn(name = "repasse_id")
    private Repasse repasse;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Pessoa paciente;

    @ManyToOne()
    @JoinColumn(name = "procedimento_id")
    private Procedimento procedimento;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;


    @CreationTimestamp
    @Column(updatable = false, name="created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAd;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name="updated_at", columnDefinition = "dateTime")
    private LocalDateTime updatedAt;


}
