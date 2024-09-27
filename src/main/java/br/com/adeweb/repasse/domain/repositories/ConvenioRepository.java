package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Convenio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConvenioRepository extends JpaRepository<Convenio, Long> {
}
