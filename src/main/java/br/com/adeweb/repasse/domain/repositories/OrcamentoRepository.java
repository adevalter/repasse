package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    @Query("SELECT o FROM Orcamento o LEFT JOIN FETCH o.itens WHERE o.id = :id")
    Optional<Orcamento> findByIdWithItens(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM Orcamento o LEFT JOIN FETCH o.itens WHERE o.chave = :chave")
    Optional<Orcamento> findOrcamentoByChave(@Param("chave") String chave);
}
