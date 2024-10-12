package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.RepasseDTO;
import br.com.adeweb.repasse.domain.entities.Repasse;
import br.com.adeweb.repasse.domain.repositories.RepasseRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class RepasseService {

    @Autowired
    private RepasseRepository repasseRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<RepasseDTO> findAll(Pageable pageable){
        Page<Repasse> repasses = repasseRepository.findAll(pageable);
        return repasses.map(repasse -> convertToDto(repasse));
    }

    public RepasseDTO buscarPorId(Long id){
        Repasse repasse = repasseRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDto(repasse);
    }

    public RepasseDTO salvarRepasse(RepasseDTO repasseDTO){
        Repasse repasse = convertToRepasse(repasseDTO);
        repasse.setStatus(1);
        repasse = repasseRepository.save(repasse);
        return convertToDto(repasse);

    }

    public RepasseDTO autualizaRepasse(Long id, RepasseDTO repasseDTO){
        Repasse repasse = convertToRepasse(repasseDTO);
        repasse.setId(id);
        repasse = repasseRepository.save(repasse);
        return convertToDto(repasse);
    }


    private  RepasseDTO convertToDto(Repasse repasse){
        return modelMapper.map(repasse, RepasseDTO.class);
    }

    private  Repasse convertToRepasse(RepasseDTO repasseDTO){
        return modelMapper.map(repasseDTO,Repasse.class);
    }
}
