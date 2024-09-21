package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.EmpresaDTO;
import br.com.adeweb.repasse.domain.entities.Empresa;
import br.com.adeweb.repasse.domain.repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {
    @Autowired
    private  EmpresaRepository empresaRepository;


    public Page<EmpresaDTO> findAll(Pageable pageable) {
        Page<Empresa> empresas = empresaRepository.findAll(pageable);
        return empresas.map(empresa -> new EmpresaDTO(empresa.getId(),
                empresa.getRazao(), empresa.getFantasia(),
                empresa.getEmail(),empresa.getCelular(),empresa.getStatus()));
    }
    public Empresa salvar(Empresa empresa){

        return empresa;
    }
}
