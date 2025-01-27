package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.RepasseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface RepasseItemRepository extends JpaRepository<RepasseItem, Long> {
    List<RepasseItem> findByRepasseId(Long id);

    @Query("Select sum(ri.valor) From RepasseItem ri WHERE ri.repasse.id = :repasseId and ri.status = :status" )
    Double totalItens(@Param("repasseId") Long repasseId, @Param("status") int status);

}
