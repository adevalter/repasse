package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.data.models.RelatorioPagamentoDTO;
import br.com.adeweb.repasse.domain.entities.Pagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    /*
     @Query("Select sum(ri.valor) From RepasseItem ri WHERE ri.repasse.id = :repasseId and ri.status = :status" )
    Double totalItens(@Param("repasseId") Long repasseId, @Param("status") int status);
     */
    Page<Pagamento> findAllByStatusLessThanOrderByIdDesc(int status,Pageable pageable);

    @Query("Select new br.com.adeweb.repasse.data.models.RelatorioPagamentoDTO( " +
            "p.bancoPagamento, p.dataDeposito, p.status, m.nome, m.email, ri.dataProcedimento, ri.valor, r.id, " +
            "pro.descricao, pa.nome, co.descricao) " +
            "from Pagamento p " +
            "inner join Repasse r on r.id = p.repasse.id " +
            "inner join Pessoa m on m.id = r.medico.id " +
            "inner join RepasseItem ri on ri.repasse.id = r.id " +
            "inner join Procedimento pro on pro.id = ri.procedimento.id " +
            "inner join Pessoa pa on pa.id = ri.paciente.id " +
            "inner join Convenio co on co.id = pa.convenio.id " +
            "where p.id = :pagamentoId and ri.status = 1  order by ri.dataProcedimento asc")
    List<RelatorioPagamentoDTO> findByPagamentoId(@Param("pagamentoId") Long pagamentoId);

    @Query("Select p  " +
            "from Pagamento p inner join User u  on u.id = p.user.id " +
            "inner join Repasse r on r.id = p.repasse.id "+
            "inner join Pessoa m on m.id = r.medico.id  "+
            "where LOWER(m.nome) LIKE LOWER(CONCAT(:nome, '%')) and m.tipoPessoa.Id = :tipoPessoa_id")
    Page<Pagamento> findByNome(String nome, Long tipoPessoa_id, Pageable pageable );
}
/*
SELECT p.banco_pagamento, p.data_deposito, p.total, p.status, m.nome as medico, ri.data_procedimento, ri.valor,ri.repasse_id, (SELECT SUM(rix.valor)
from awrepasse.repasse_item rix WHERE rix.repasse_id = r.id) totalitem,
pro.descricao as procedimento, pa.nome as paciente, c.descricao as convenio
from awrepasse.pagamento p inner join awrepasse.repasse r on r.id = p.repasse_id
inner join awrepasse.pessoa m on m.id = r.medico_id
inner join awrepasse.repasse_item ri on ri.repasse_id = r.id
inner join awrepasse.procedimento pro on pro.id = ri.procedimento_id
inner join awrepasse.pessoa pa on pa.id = ri.paciente_id
inner join awrepasse.convenio c on c.id  = pa.convenio_id
WHERE p.id = 3
 */