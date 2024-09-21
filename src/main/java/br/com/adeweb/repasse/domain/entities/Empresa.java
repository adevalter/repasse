package br.com.adeweb.repasse.domain.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
public class Empresa {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    
    
    private String razao;

    private String fantasia;
    
    private String email;
    
    private String celular;
    
    private int status;
    
    @CreationTimestamp
    @Column(nullable = false, name = "created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at", columnDefinition = "dateTime")
    private LocalDateTime updateAt;


}

/*
 *  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `razao` varchar(255) NOT NULL DEFAULT '',
  `fantasia` varchar(255) NOT NULL DEFAULT '',
  `email` varchar(255) NOT NULL DEFAULT '',
  `document` varchar(50) DEFAULT NULL,
  `celular` varchar(20) DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'registered' COMMENT 'registered, confirmed',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` 
 */