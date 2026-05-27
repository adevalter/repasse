package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.data.models.DespesaMedicoDTO;
import br.com.adeweb.repasse.domain.entities.DespesaMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DespesaMedicoRepository extends JpaRepository<DespesaMedico,Long> {
    List<DespesaMedico> findByRepasseId(Long id);
}
