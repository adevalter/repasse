package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.PessoaDTO;
import br.com.adeweb.repasse.domain.entities.Pessoa;
import br.com.adeweb.repasse.domain.repositories.PessoaRepositoy;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {
    @Autowired
    private PessoaRepositoy pessoaRepositoy;

    @Autowired
    private ModelMapper modelMapper;

    public Page<PessoaDTO> findAll(Pageable pageable){
        Page<Pessoa> pessoas = pessoaRepositoy.findAll(pageable);
        return pessoas.map(pessoa -> convertToDTO(pessoa));
    }

    public PessoaDTO buscarPorId(Long id){
        Pessoa pessoa = pessoaRepositoy.findById(id).orElseThrow(EntityNotFoundException::new);
        return convertToDTO(pessoa);
    }

    public PessoaDTO salvarPessoa(PessoaDTO pessoaDTO){
        Pessoa pessoa = convertToPessoa(pessoaDTO);
        pessoa.setStatus(1);
        pessoaRepositoy.save(pessoa);
        return convertToDTO(pessoa);
    }

    public PessoaDTO atualizarPessoa(Long id, PessoaDTO pessoaDTO){
        Pessoa pessoa = convertToPessoa(pessoaDTO);
        pessoa = pessoaRepositoy.save(pessoa);
        return convertToDTO(pessoa);
    }

    private PessoaDTO convertToDTO(Pessoa pessoa){
        return modelMapper.map(pessoa, PessoaDTO.class);
    }

    private Pessoa convertToPessoa(PessoaDTO pessoaDTO){
        return modelMapper.map(pessoaDTO, Pessoa.class);
    }
}
