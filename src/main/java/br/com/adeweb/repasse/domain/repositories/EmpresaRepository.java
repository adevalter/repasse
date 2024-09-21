package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.data.models.EmpresaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.adeweb.repasse.domain.entities.Empresa;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
@Repository
public interface EmpresaRepository  extends JpaRepository<Empresa, Long> {

}
