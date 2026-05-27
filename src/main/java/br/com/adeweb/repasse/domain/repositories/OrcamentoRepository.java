package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento,Long> {
    @Query("Select o from Orcamento o inner join User u on u.id = o.aprovado.id where o.chave = :chave")
    Optional<Orcamento> findOrcamentoByChave(String chave);
}
