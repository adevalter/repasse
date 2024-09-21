package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.EmpresaDTO;
import br.com.adeweb.repasse.domain.entities.Empresa;
import br.com.adeweb.repasse.domain.repositories.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {
    @Autowired
    private  EmpresaRepository empresaRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Page<EmpresaDTO> findAll(Pageable pageable) {
        Page<Empresa> empresas = empresaRepository.findAll(pageable);
        return empresas.map(empresa -> convertToDto(empresa));
    }

    public EmpresaDTO buscaPorId(Long id){
        Empresa empresa = empresaRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return convertToDto(empresa);
    }

    public EmpresaDTO salvar(EmpresaDTO empresaDTO){
        Empresa empresa = convertToEmpresa(empresaDTO);
        empresa.setStatus(1);
        empresaRepository.save(empresa);

        return convertToDto(empresa);
    }

    public EmpresaDTO atualizaEmpresa(Long id, EmpresaDTO empresaDTO){
        Empresa empresa = convertToEmpresa(empresaDTO);
        empresa.setId(id);
        empresa = empresaRepository.save(empresa);
        return convertToDto(empresa);
    }

    private EmpresaDTO convertToDto(Empresa empresa){
        return modelMapper.map(empresa, EmpresaDTO.class);
    }

    private Empresa convertToEmpresa(EmpresaDTO empresaDto){
        return modelMapper.map(empresaDto, Empresa.class);
    }
//    private void excluirEmpresa(Long id){
//        empresaRepository.deleteById(id);
//    }
}
