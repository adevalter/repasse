package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.data.models.DespesaMedicoDTO;
import br.com.adeweb.repasse.domain.entities.DespesaMedico;
import br.com.adeweb.repasse.domain.repositories.DespesaMedicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DespesaMedicoService {
  private final DespesaMedicoRepository despesaMedicoRepository;
    private final ModelMapper modelMapper;

    public DespesaMedicoService(DespesaMedicoRepository despesaMedicoRepository, ModelMapper modelMapper) {
        this.despesaMedicoRepository = despesaMedicoRepository;
        this.modelMapper = modelMapper;
    }


    public Page<DespesaMedicoDTO> findAll(Pageable pageable){
        Page<DespesaMedico> despesaMedicos = despesaMedicoRepository.findAll(pageable);
        return despesaMedicos.map(despesaMedico -> convertToDTO(despesaMedico));
    }


    public DespesaMedicoDTO buscarPorId(Long id){
        DespesaMedico pessoa = despesaMedicoRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return convertToDTO(pessoa);
    }

    public DespesaMedicoDTO salvar(DespesaMedicoDTO despesaMedicoDTO){
        DespesaMedico despesaEntity = convertToEntity(despesaMedicoDTO);
        despesaEntity.setStatus(1);
        DespesaMedico despesaMedico = despesaMedicoRepository.save(despesaEntity);
        return convertToDTO(despesaMedico);
    }

    public DespesaMedicoDTO atualizar(Long id, DespesaMedicoDTO despesaMedicoDTO){
        DespesaMedico entity = despesaMedicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        entity.setDescricao(despesaMedicoDTO.getDescricao());
        entity.setValor(despesaMedicoDTO.getValor());
        DespesaMedico updated = despesaMedicoRepository.save(entity);
        return convertToDTO(updated);
    }

    public void excluir(Long id){
        try {
            despesaMedicoRepository.deleteById(id);
            despesaMedicoRepository.flush();
        }catch (EmptyResultDataAccessException e){
            throw new EmptyResultDataAccessException(e.getExpectedSize());
        }
    }
    private DespesaMedicoDTO convertToDTO(DespesaMedico despesaMedico){
        return modelMapper.map(despesaMedico, DespesaMedicoDTO.class);
    }
    private DespesaMedico convertToEntity(DespesaMedicoDTO despesaMedicoDTO){
        return modelMapper.map(despesaMedicoDTO, DespesaMedico.class);
    }
}
