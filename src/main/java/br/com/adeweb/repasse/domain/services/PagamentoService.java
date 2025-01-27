package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.PagamentoDTO;
import br.com.adeweb.repasse.domain.entities.Pagamento;
import br.com.adeweb.repasse.domain.entities.Repasse;
import br.com.adeweb.repasse.domain.repositories.PagamentoRepository;
import br.com.adeweb.repasse.domain.repositories.RepasseRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private RepasseRepository repasseRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Page<PagamentoDTO> findAll(Pageable pageable){
        Page<Pagamento> pagamentos = pagamentoRepository.findAll(pageable);
        return pagamentos.map(pagamento -> convetToDto(pagamento));
    }

    public PagamentoDTO buscarById(Long id){
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convetToDto(pagamento);
    }

    public PagamentoDTO salvarPagamento(PagamentoDTO pagamentoDTO){
        Pagamento pagamento = convertToPagamento(pagamentoDTO);
        pagamentoRepository.save(pagamento);
        return convetToDto(pagamento);
    }

    public  Page<PagamentoDTO> findByName(String nome, Long tipoPessoaId, Pageable pageable){
        Page<Pagamento> pagamentos = pagamentoRepository.findByNome(nome, tipoPessoaId, pageable);
        return pagamentos.map(this::convetToDto);
    }

    public void excluir(Long id, Long repasseId){

        try {

            Repasse repasse = repasseRepository.findById(repasseId).orElse(null);
            if (repasse != null) {
                repasse.setStatus(1);
                repasseRepository.save(repasse);
            }
            pagamentoRepository.deleteById(id);
            pagamentoRepository.flush();

        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException(e.getExpectedSize());
        }


    }

    private PagamentoDTO convetToDto(Pagamento pagamento) {
        return  modelMapper.map(pagamento, PagamentoDTO.class);
    }

    private Pagamento convertToPagamento(PagamentoDTO pagamentoDTO){
        return modelMapper.map(pagamentoDTO,Pagamento.class);
    }

}
