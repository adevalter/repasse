package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.ConvenioDTO;
import br.com.adeweb.repasse.domain.entities.Convenio;
import br.com.adeweb.repasse.domain.repositories.ConvenioRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConvenioService {
    @Autowired
    private ConvenioRepository convenioRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<ConvenioDTO> findAll(Pageable pageable){
        Page<Convenio> convenios = convenioRepository.findAll(pageable);
        return convenios.map(convenio -> convertToDto(convenio));
    }

    public ConvenioDTO buscarPorId(Long id){
        Convenio convenio = convenioRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDto(convenio);
    }

    public ConvenioDTO salvarConvenio(ConvenioDTO convenioDTO){
        Convenio convenio = convertToConvenio(convenioDTO);
        convenio.setStatus(1);
        convenioRepository.save(convenio);
        return convertToDto(convenio);
    }

    public ConvenioDTO autualizaConvenio(Long id, ConvenioDTO convenioDTO){
        Convenio convenio = convertToConvenio(convenioDTO);
        convenio.setId(id);
        convenio = convenioRepository.save(convenio);
        return convertToDto(convenio);
    }


    private ConvenioDTO convertToDto(Convenio convenio){
        return modelMapper.map(convenio, ConvenioDTO.class);
    }

    private Convenio convertToConvenio(ConvenioDTO convenioDTO){
        return modelMapper.map(convenioDTO, Convenio.class);

    }
}
