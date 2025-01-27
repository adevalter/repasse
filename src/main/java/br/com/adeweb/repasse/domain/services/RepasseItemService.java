package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.RepasseItemDTO;
import br.com.adeweb.repasse.domain.entities.RepasseItem;
import br.com.adeweb.repasse.domain.repositories.RepasseItemRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class RepasseItemService {

    @Autowired
    private RepasseItemRepository repasseItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<RepasseItemDTO> findAll(Pageable pageable){
        Page<RepasseItem> repasseItems = repasseItemRepository.findAll(pageable);
        return repasseItems.map(repasseItem -> convertToDto(repasseItem));
    }



    public RepasseItemDTO buscarPorId(Long id){
        RepasseItem repasseItem = repasseItemRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDto(repasseItem);
    }
    public List<RepasseItemDTO> buscarRepassePorId(Long id){
        List<RepasseItem> repasseItem = repasseItemRepository.findByRepasseId(id);
        return repasseItem.stream()
                .map(repasseItem1-> convertToDto(repasseItem1)).toList();

    }

    public RepasseItemDTO salvarRepasseItem(RepasseItemDTO repasseItemDTO){
        RepasseItem repasseItem = convertToRepasseItem(repasseItemDTO);
        repasseItem.setStatus(1);
        repasseItem = repasseItemRepository.save(repasseItem);
        return convertToDto(repasseItem);
    }

    public RepasseItemDTO autualizarRepasseItem(Long id, RepasseItemDTO repasseItemDTO){
        RepasseItem repasseItem = convertToRepasseItem(repasseItemDTO);
        repasseItem.setId(id);
        repasseItem = repasseItemRepository.save(repasseItem);
        return  convertToDto(repasseItem);
    }

    public boolean updateStatus(Long id, int status) {
        RepasseItem repasseItem = repasseItemRepository.findById(id).orElse(null);
        if (repasseItem != null) {
            repasseItem.setStatus(status);
            repasseItemRepository.save(repasseItem);
            return true;
        }
        return false;
    }

    public void excluir(Long id){
        try {
            repasseItemRepository.deleteById(id);
            repasseItemRepository.flush();
        }catch (EmptyResultDataAccessException e){
            throw new EmptyResultDataAccessException(e.getExpectedSize());
        }
    }

    private RepasseItemDTO convertToDto(RepasseItem repasseItem){
        return modelMapper.map(repasseItem, RepasseItemDTO.class);
    }

    private RepasseItem convertToRepasseItem(RepasseItemDTO repasseItemDTO){
        return modelMapper.map(repasseItemDTO, RepasseItem.class);
    }



}
