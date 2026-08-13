package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.OrcamentoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrcamentoItemRepository extends JpaRepository<OrcamentoItem, Long> {
}
