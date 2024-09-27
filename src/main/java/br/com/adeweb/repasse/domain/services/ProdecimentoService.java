package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.ConvenioDTO;
import br.com.adeweb.repasse.data.models.ProcedimentoDTO;
import br.com.adeweb.repasse.domain.entities.Procedimento;
import br.com.adeweb.repasse.domain.repositories.ProcedimentoRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ProdecimentoService {
    @Autowired
    ProcedimentoRepository procedimentoRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<ProcedimentoDTO> findAll(Pageable pageable){
        Page<Procedimento> procedimentos = procedimentoRepository.findAll(pageable);
        return procedimentos.map(procedimento -> convertToDto(procedimento));
    }

    public ProcedimentoDTO buscaPorId(Long id){
        Procedimento procedimento = procedimentoRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDto(procedimento);
    }

    public ProcedimentoDTO salvarProcedimento(ProcedimentoDTO procedimentoDTO){
        Procedimento procedimento = convertToProcedimento(procedimentoDTO);
        procedimento.setStatus(1);
        return convertToDto(procedimentoRepository.save(procedimento));
    }



    private ProcedimentoDTO convertToDto(Procedimento procedimento){
        return modelMapper.map(procedimento, ProcedimentoDTO.class);
    }

    private Procedimento convertToProcedimento(ProcedimentoDTO procedimentoDTO){
        return modelMapper.map(procedimentoDTO, Procedimento.class);
    }
}
