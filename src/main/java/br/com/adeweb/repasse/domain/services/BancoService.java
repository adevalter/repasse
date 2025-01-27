package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.BancoDTO;
import br.com.adeweb.repasse.data.models.PagamentoDTO;
import br.com.adeweb.repasse.domain.entities.Banco;
import br.com.adeweb.repasse.domain.entities.Pagamento;
import br.com.adeweb.repasse.domain.repositories.BancoRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BancoService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BancoRepository bancoRepository;

    public List<BancoDTO> findAll(){
        List<Banco> bancos = bancoRepository.findAll();
        return bancos.stream().map(this::convertToDto).toList();
    }

    public BancoDTO buscarById(Long id){
        Banco banco = bancoRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDto(banco);
    }

    public BancoDTO salvarBanco(BancoDTO bancoDTO){
        Banco banco = convertToEntity(bancoDTO);
        bancoRepository.save(banco);
        return  convertToDto(banco);
    }

    private BancoDTO convertToDto(Banco banco){
        return modelMapper.map(banco, BancoDTO.class);
    }

    private Banco convertToEntity(BancoDTO bancoDTO){
        return modelMapper.map(bancoDTO, Banco.class);
    }

}
