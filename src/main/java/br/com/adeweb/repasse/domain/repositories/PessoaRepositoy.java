package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Pessoa;
import br.com.adeweb.repasse.domain.entities.TipoPessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PessoaRepositoy extends JpaRepository<Pessoa, Long> {
    Page<Pessoa> findByTipoPessoaId(Pageable pageable, Long id);

    @Query("Select p from Pessoa p left join Convenio c on c.id = p.convenio.id " +
            " inner join TipoPessoa tp on tp.id = p.tipoPessoa.id where p.id = :id")
    Optional<Pessoa> findMedicoById(Long id);


    @Query("Select p from Pessoa p left join Convenio c on c.id = p.convenio.id " +
            " where LOWER(p.nome) LIKE LOWER(CONCAT(:nome, '%')) and p.tipoPessoa.Id = :tipoPessoa_id")
    Page<Pessoa> findByNome(String nome, Long tipoPessoa_id, Pageable pageable );
}
