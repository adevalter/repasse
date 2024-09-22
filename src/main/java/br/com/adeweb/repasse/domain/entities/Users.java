package br.com.adeweb.repasse.domain.entities;

import br.com.adeweb.repasse.domain.enums.StatusUsers;
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
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String password;
    private String forget;
    private String document;
    private String celular;

    @Enumerated(EnumType.STRING)
    private StatusUsers status;
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @CreationTimestamp
    @Column(name="created_at", nullable = false, columnDefinition = "dateTime")
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name="updated_at", nullable = false, columnDefinition = "dateTime")
    private LocalDateTime updatedAt;



}
