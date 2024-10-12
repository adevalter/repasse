package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Repasse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepasseRepository extends JpaRepository<Repasse, Long> {
}
