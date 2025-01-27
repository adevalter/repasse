package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Procedimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
    //
    @Query("SELECT p FROM Procedimento p WHERE LOWER(p.descricao) LIKE LOWER(CONCAT(:descricao, '%'))")
    Page<Procedimento> findByDescricao(String descricao, Pageable pageable);
}
