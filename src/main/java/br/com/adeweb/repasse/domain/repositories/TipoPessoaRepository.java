package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.TipoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoPessoaRepository extends JpaRepository<TipoPessoa, Long > {
}
