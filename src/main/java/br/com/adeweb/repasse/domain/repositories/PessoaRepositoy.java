package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepositoy extends JpaRepository<Pessoa, Long> {
}
