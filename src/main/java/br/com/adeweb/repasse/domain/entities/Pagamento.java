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
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double total;
    private String bancoPagamento;
    private LocalDateTime dataDeposito;
    private int status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "repasse_id")
    Repasse repasse;

    @CreationTimestamp
    @Column(updatable = false, name="created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAd;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name="updated_at", columnDefinition = "dateTime")
    private LocalDateTime updatedAt;
}
