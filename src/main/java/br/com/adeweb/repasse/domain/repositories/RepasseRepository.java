package br.com.adeweb.repasse.domain.repositories;


import br.com.adeweb.repasse.domain.entities.Repasse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepasseRepository extends JpaRepository<Repasse, Long> {

    @Query("Select r  " +
            "from Repasse r inner join User u  on u.id = r.user.id " +
            "inner join Pessoa m on m.id = r.medico.id  "+
            "inner join Empresa e on e.id = r.empresa.id  "+
            "where LOWER(m.nome) LIKE LOWER(CONCAT(:nome, '%')) and m.tipoPessoa.Id = :tipoPessoa_id")
    Page<Repasse> findByNome(String nome, Long tipoPessoa_id, Pageable pageable );

}
