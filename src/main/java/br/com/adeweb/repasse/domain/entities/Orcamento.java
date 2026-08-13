package br.com.adeweb.repasse.domain.entities;

import br.com.adeweb.repasse.domain.enums.TipoOrcamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orcamento")
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoOrcamento tipo;

    @Column(name = "procedimento_cirurgico")
    private String procedimentoCirurgico;

    @Column(name = "equipe_medica")
    private String equipeMedica;

    @Column(name = "obs_pagamento")
    private String obsPagamento;

    @Column(columnDefinition = "TEXT")
    private String incluso;

    @Column(name = "nao_incluso", columnDefinition = "TEXT")
    private String naoIncluso;

    @Column(columnDefinition = "TEXT")
    private String complicacoes;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    /** Legado: orçamentos antigos em texto livre. Novos usam campos estruturados + itens. */
    @Column(columnDefinition = "TEXT")
    private String descricao;

    private int status;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC, id ASC")
    private List<OrcamentoItem> itens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "aprovado_id")
    User aprovado;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "aprovado_em", columnDefinition = "dateTime")
    private LocalDateTime aprovadoEm;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at", columnDefinition = "dateTime")
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "dateTime")
    private LocalDateTime updatedAt;

    public void adicionarItem(OrcamentoItem item) {
        itens.add(item);
        item.setOrcamento(this);
    }

    public void limparItens() {
        for (OrcamentoItem item : itens) {
            item.setOrcamento(null);
        }
        itens.clear();
    }
}
