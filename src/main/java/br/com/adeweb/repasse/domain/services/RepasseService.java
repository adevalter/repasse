package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.RepasseDTO;
import br.com.adeweb.repasse.domain.entities.Pagamento;
import br.com.adeweb.repasse.domain.entities.Repasse;
import br.com.adeweb.repasse.domain.repositories.PagamentoRepository;
import br.com.adeweb.repasse.domain.repositories.RepasseItemRepository;
import br.com.adeweb.repasse.domain.repositories.RepasseRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class RepasseService {

    @Autowired
    private RepasseRepository repasseRepository;
    @Autowired
    private RepasseItemRepository repasseItemRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private PdfService pdfService;

    @Autowired
    private ModelMapper modelMapper;

    public Page<RepasseDTO> findAll(Pageable pageable){
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("id").descending()
        );
        Page<Repasse> repasses = repasseRepository.findAll(sortedPageable);
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

    public boolean updateStatus(Long id, int status){
        Repasse repasse = repasseRepository.findById(id).orElse(null);
        if(repasse!= null){
            double total = repasseItemRepository.totalItens(repasse.getId(),1);
            Pagamento pagamento = new Pagamento();
            pagamento.setRepasse(repasse);
            pagamento.setTotal(total);
            pagamento.setUser(repasse.getUser());
            pagamento.setStatus(1);
            pagamentoRepository.save(pagamento);

            repasse.setStatus(status);
            repasseRepository.save(repasse);
            return true;
        }
        return false;
    }

    public byte[] finalizarComPdf(Long id, int status){
        Repasse repasse = repasseRepository.findById(id).orElse(null);
        if(repasse!= null){
            double total = repasseItemRepository.totalItens(repasse.getId(),1);
            Pagamento pagamento = new Pagamento();
            pagamento.setRepasse(repasse);
            pagamento.setTotal(total);
            pagamento.setUser(repasse.getUser());
            pagamento.setStatus(1);
            var resultado = pagamentoRepository.save(pagamento);

            repasse.setStatus(status);
            repasseRepository.save(repasse);

            return pdfService.gerarPdf(resultado.getId());


        }
        return null;
    }

    /*
     public boolean updateStatus(Long id, int status) {
        RepasseItem repasseItem = repasseItemRepository.findById(id).orElse(null);
        if (repasseItem != null) {
            repasseItem.setStatus(status);
            repasseItemRepository.save(repasseItem);
            return true;
        }
        return false;
    }
     */

    public RepasseDTO autualizaRepasse(Long id, RepasseDTO repasseDTO){
        Repasse repasse = convertToRepasse(repasseDTO);
        repasse.setId(id);
        repasse = repasseRepository.save(repasse);
        return convertToDto(repasse);
    }

    public  Page<RepasseDTO> findByName(String nome, Long tipoPessoaId, Pageable pageable){
        Page<Repasse> repasses = repasseRepository.findByNome(nome, tipoPessoaId, pageable);
        return repasses.map(this::convertToDto);
    }

    private  RepasseDTO convertToDto(Repasse repasse){
        return modelMapper.map(repasse, RepasseDTO.class);
    }

    private  Repasse convertToRepasse(RepasseDTO repasseDTO){
        return modelMapper.map(repasseDTO,Repasse.class);
    }
}
