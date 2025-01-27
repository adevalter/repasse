package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Convenio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConvenioRepository extends JpaRepository<Convenio, Long> {
    @Query("Select c FROM Convenio c where LOWER(c.descricao) like LOWER(CONCAT(:descricao, '%'))")
    Page<Convenio> findByDescricao(String descricao, Pageable pageable);
}
