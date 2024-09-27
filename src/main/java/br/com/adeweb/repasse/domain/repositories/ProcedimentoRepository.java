package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
}
