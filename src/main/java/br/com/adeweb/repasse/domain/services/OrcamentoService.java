package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.core.utils.UtilStrings;
import br.com.adeweb.repasse.data.models.OrcamentoDTO;
import br.com.adeweb.repasse.domain.entities.Orcamento;
import br.com.adeweb.repasse.domain.entities.User;
import br.com.adeweb.repasse.domain.repositories.OrcamentoRepository;
import br.com.adeweb.repasse.domain.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrcamentoService {


    private final OrcamentoRepository orcamentoRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public OrcamentoService(OrcamentoRepository orcamentoRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public Page<OrcamentoDTO> findAll(Pageable pageable){
        Page<Orcamento> orcamentos = orcamentoRepository.findAll(pageable);
        return orcamentos.map(this::convertToDto);

    }

    public OrcamentoDTO buscaPorid(Long id){
        Orcamento orcamento = orcamentoRepository.findById(id).orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        return  convertToDto(orcamento);
    }

    public OrcamentoDTO salvarOrcamento(OrcamentoDTO orcamentoDTO){
        Orcamento orcamento = convertToOrcamento(orcamentoDTO);
        orcamento.setStatus(1);
        orcamento.setAprovadoEm(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        var salvo = orcamentoRepository.save(orcamento);


        return convertToDto(updateToken(salvo.getId()));
    }

    public OrcamentoDTO buscaProAprovado(String chave){
        Orcamento orcamento = orcamentoRepository.findOrcamentoByChave(chave).orElseThrow(EntityNotFoundException::new);
        return convertToDto(orcamento);
    }

    public OrcamentoDTO aprovarOrcamento(Long idOrcamento, Long idUsuarioAprovador) {
        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        User aprovador = userRepository.findById(idUsuarioAprovador)
                .orElseThrow(() -> new RuntimeException("Usuário aprovador não encontrado"));

        // Gera o token

        orcamento.setAprovado(aprovador);
        orcamento.setAprovadoEm(LocalDateTime.now());
        orcamento.setStatus(2);

        //status 1 = criado, status = 2 aprovado , status 3 cancelado(excluido)
        Orcamento atualizado = orcamentoRepository.save(orcamento);

        return convertToDto(atualizado);
    }
    public  boolean updateStatusOrcamento(long idOrcamento, int status, long userId){
        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        User canceladoPor = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        orcamento.setStatus(status);
        orcamento.setAprovadoEm(LocalDateTime.now());
        orcamento.setAprovado(canceladoPor);
        orcamentoRepository.save(orcamento);
        return true;
    }

    public Orcamento updateToken(long idOrcamento){
        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        String token = UtilStrings.gerarToken(orcamento.getId());
        orcamento.setChave(token);
        orcamento.setAprovadoEm(LocalDateTime.now());
        return(orcamentoRepository.save(orcamento));

    }

    private OrcamentoDTO convertToDto(Orcamento orcamento){
        return modelMapper.map(orcamento, OrcamentoDTO.class);
    }

    private Orcamento convertToOrcamento(OrcamentoDTO orcamentoDTO){
        return modelMapper.map(orcamentoDTO, Orcamento.class);
    }
}
