package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {
}
