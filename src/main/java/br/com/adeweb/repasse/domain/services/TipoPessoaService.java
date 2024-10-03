package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.TipoPessoaDTO;
import br.com.adeweb.repasse.domain.entities.TipoPessoa;
import br.com.adeweb.repasse.domain.repositories.TipoPessoaRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TipoPessoaService {
    @Autowired
    private TipoPessoaRepository tipoPessoaRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<TipoPessoaDTO> findAll(Pageable pageable){
        Page<TipoPessoa> tipoPessoas = tipoPessoaRepository.findAll(pageable);
        return tipoPessoas.map(tipoPessoa -> convertToDto(tipoPessoa));
    }
    public TipoPessoaDTO buscarPorId(Long id){
        TipoPessoa tipoPessoa = tipoPessoaRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDto(tipoPessoa);
    }

    public TipoPessoaDTO salvarTipoPessoa(TipoPessoaDTO tipoPessoaDTO){
        TipoPessoa tipoPessoa = convertToTipoPessoa(tipoPessoaDTO);
        tipoPessoa.setStatus(1);
        tipoPessoaRepository.save(tipoPessoa);
        return convertToDto(tipoPessoa);
    }

    public TipoPessoaDTO atualizarTipoPessoa(TipoPessoaDTO tipoPessoaDTO){
        TipoPessoa tipoPessoa = convertToTipoPessoa(tipoPessoaDTO);
        tipoPessoa.setStatus(1);
        tipoPessoa = tipoPessoaRepository.save(tipoPessoa);
        return convertToDto(tipoPessoa);
    }

    private TipoPessoaDTO convertToDto(TipoPessoa tipoPessoa){
        return  modelMapper.map(tipoPessoa,TipoPessoaDTO.class);
    }

    private TipoPessoa convertToTipoPessoa(TipoPessoaDTO tipoPessoaDTO){
        return modelMapper.map(tipoPessoaDTO, TipoPessoa.class);
    }

}
