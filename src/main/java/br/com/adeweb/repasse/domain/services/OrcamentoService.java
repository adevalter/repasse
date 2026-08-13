package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.core.utils.UtilStrings;
import br.com.adeweb.repasse.data.models.OrcamentoDTO;
import br.com.adeweb.repasse.data.models.OrcamentoItemDTO;
import br.com.adeweb.repasse.domain.entities.Orcamento;
import br.com.adeweb.repasse.domain.entities.OrcamentoItem;
import br.com.adeweb.repasse.domain.entities.User;
import br.com.adeweb.repasse.domain.enums.SecaoOrcamentoItem;
import br.com.adeweb.repasse.domain.enums.TipoOrcamento;
import br.com.adeweb.repasse.domain.repositories.OrcamentoRepository;
import br.com.adeweb.repasse.domain.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    @Transactional(readOnly = true)
    public Page<OrcamentoDTO> findAll(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("id").descending()
        );
        return orcamentoRepository.findAll(sorted).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public OrcamentoDTO buscaPorid(Long id) {
        return convertToDto(buscarComItens(id));
    }

    @Transactional
    public OrcamentoDTO salvarOrcamento(OrcamentoDTO orcamentoDTO) {
        if (orcamentoDTO.getTipo() == null) {
            throw new IllegalArgumentException("Tipo do orçamento é obrigatório (CESAREA ou GERAL)");
        }

        Orcamento orcamento = new Orcamento();
        aplicarDadosCabecalho(orcamento, orcamentoDTO);
        aplicarDefaultsSeVazio(orcamento, orcamentoDTO.getTipo());
        substituirItens(orcamento, orcamentoDTO.getItens());

        orcamento.setStatus(1);
        orcamento.setAprovadoEm(null);
        orcamento.setAprovado(null);
        // chave é NOT NULL no banco; placeholder até gerar o token com o id
        orcamento.setChave("");

        Orcamento salvo = orcamentoRepository.save(orcamento);
        return convertToDto(updateToken(salvo.getId()));
    }

    @Transactional
    public OrcamentoDTO atualizarOrcamento(Long id, OrcamentoDTO orcamentoDTO) {
        Orcamento orcamento = buscarComItens(id);

        if (orcamentoDTO.getTipo() != null) {
            orcamento.setTipo(orcamentoDTO.getTipo());
        }
        if (orcamento.getTipo() == null) {
            throw new IllegalArgumentException("Tipo do orçamento é obrigatório (CESAREA ou GERAL)");
        }

        aplicarDadosCabecalho(orcamento, orcamentoDTO);
        aplicarDefaultsSeVazio(orcamento, orcamento.getTipo());
        substituirItens(orcamento, orcamentoDTO.getItens());

        return convertToDto(orcamentoRepository.save(orcamento));
    }

    @Transactional(readOnly = true)
    public OrcamentoDTO buscaProAprovado(String chave) {
        Orcamento orcamento = orcamentoRepository.findOrcamentoByChave(chave)
                .orElseThrow(EntityNotFoundException::new);
        return convertToDto(orcamento);
    }

    @Transactional
    public OrcamentoDTO aprovarOrcamento(Long idOrcamento, Long idUsuarioAprovador) {
        Orcamento orcamento = buscarComItens(idOrcamento);

        User aprovador = userRepository.findById(idUsuarioAprovador)
                .orElseThrow(() -> new EntityNotFoundException("Usuário aprovador não encontrado"));

        orcamento.setAprovado(aprovador);
        orcamento.setAprovadoEm(LocalDateTime.now());
        orcamento.setStatus(2);

        return convertToDto(orcamentoRepository.save(orcamento));
    }

    @Transactional
    public boolean updateStatusOrcamento(long idOrcamento, int status, long userId) {
        Orcamento orcamento = buscarComItens(idOrcamento);

        User canceladoPor = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        orcamento.setStatus(status);
        orcamento.setAprovadoEm(LocalDateTime.now());
        orcamento.setAprovado(canceladoPor);
        orcamentoRepository.save(orcamento);
        return true;
    }

    @Transactional
    public Orcamento updateToken(long idOrcamento) {
        Orcamento orcamento = buscarComItens(idOrcamento);
        String token = UtilStrings.gerarToken(orcamento.getId());
        orcamento.setChave(token);
        return orcamentoRepository.save(orcamento);
    }

    private Orcamento buscarComItens(Long id) {
        return orcamentoRepository.findByIdWithItens(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado"));
    }

    private void aplicarDadosCabecalho(Orcamento orcamento, OrcamentoDTO dto) {
        if (dto.getTipo() != null) {
            orcamento.setTipo(dto.getTipo());
        }
        if (dto.getNome() != null) {
            orcamento.setNome(dto.getNome());
        }
        if (dto.getSexo() != null) {
            orcamento.setSexo(dto.getSexo());
        }
        if (dto.getEmail() != null) {
            orcamento.setEmail(dto.getEmail());
        }
        if (dto.getCelular() != null) {
            orcamento.setCelular(dto.getCelular());
        }
        if (dto.getProcedimentoCirurgico() != null) {
            orcamento.setProcedimentoCirurgico(dto.getProcedimentoCirurgico());
        }
        if (dto.getEquipeMedica() != null) {
            orcamento.setEquipeMedica(dto.getEquipeMedica());
        }
        if (dto.getObsPagamento() != null) {
            orcamento.setObsPagamento(dto.getObsPagamento());
        }
        if (dto.getIncluso() != null) {
            orcamento.setIncluso(dto.getIncluso());
        }
        if (dto.getNaoIncluso() != null) {
            orcamento.setNaoIncluso(dto.getNaoIncluso());
        }
        if (dto.getComplicacoes() != null) {
            orcamento.setComplicacoes(dto.getComplicacoes());
        }
        if (dto.getObservacoes() != null) {
            orcamento.setObservacoes(dto.getObservacoes());
        }
        if (dto.getDescricao() != null) {
            orcamento.setDescricao(dto.getDescricao());
        }
        if (dto.getUser() != null && dto.getUser().getId() != null) {
            userRepository.findById(dto.getUser().getId()).ifPresent(orcamento::setUser);
        }
    }

    private void aplicarDefaultsSeVazio(Orcamento orcamento, TipoOrcamento tipo) {
        if (isBlank(orcamento.getObsPagamento())) {
            orcamento.setObsPagamento(OrcamentoTextos.OBS_PAGAMENTO_PADRAO);
        }
        if (isBlank(orcamento.getIncluso())) {
            orcamento.setIncluso(OrcamentoTextos.incluso(tipo));
        }
        if (isBlank(orcamento.getNaoIncluso())) {
            orcamento.setNaoIncluso(OrcamentoTextos.naoIncluso(tipo));
        }
        if (isBlank(orcamento.getComplicacoes())) {
            orcamento.setComplicacoes(OrcamentoTextos.COMPLICACOES_PADRAO);
        }
        if (isBlank(orcamento.getObservacoes())) {
            orcamento.setObservacoes(OrcamentoTextos.observacoes());
        }
    }

    private void substituirItens(Orcamento orcamento, List<OrcamentoItemDTO> itensDto) {
        orcamento.limparItens();
        List<OrcamentoItemDTO> origem = (itensDto == null || itensDto.isEmpty())
                ? seedItensPadrao(orcamento.getTipo())
                : itensDto;

        int ordemFallback = 0;
        for (OrcamentoItemDTO itemDto : origem) {
            if (itemDto.getSecao() == null || isBlank(itemDto.getDescricao())) {
                continue;
            }
            OrcamentoItem item = new OrcamentoItem();
            item.setSecao(itemDto.getSecao());
            item.setDescricao(itemDto.getDescricao().trim());
            item.setValor(itemDto.getValor() != null ? itemDto.getValor() : BigDecimal.ZERO);
            item.setOrdem(itemDto.getOrdem() > 0 ? itemDto.getOrdem() : ordemFallback++);
            orcamento.adicionarItem(item);
        }
    }

    private List<OrcamentoItemDTO> seedItensPadrao(TipoOrcamento tipo) {
        List<OrcamentoItemDTO> itens = new ArrayList<>();
        itens.add(item(SecaoOrcamentoItem.EQUIPE, "CIRURGIÃO", 0));
        itens.add(item(SecaoOrcamentoItem.EQUIPE, "AUXILIAR", 1));
        itens.add(item(SecaoOrcamentoItem.EQUIPE, "PEDIATRA", 2));
        itens.add(item(SecaoOrcamentoItem.EQUIPE, "ANESTESISTA", 3));
        itens.add(item(SecaoOrcamentoItem.EQUIPE, "INSTRUMENTADOR", 4));
        itens.add(item(SecaoOrcamentoItem.EQUIPE, "LABORATÓRIO", 5));
        if (tipo == TipoOrcamento.GERAL) {
            itens.add(item(SecaoOrcamentoItem.EQUIPE, "ANATOMOPATOLÓGICO", 6));
            itens.add(item(SecaoOrcamentoItem.HOSPITALAR, "DESPESAS HOSPITALARES", 7));
        } else {
            itens.add(item(SecaoOrcamentoItem.HOSPITALAR, "DESPESAS HOSPITALARES", 6));
        }
        return itens;
    }

    private OrcamentoItemDTO item(SecaoOrcamentoItem secao, String descricao, int ordem) {
        OrcamentoItemDTO dto = new OrcamentoItemDTO();
        dto.setSecao(secao);
        dto.setDescricao(descricao);
        dto.setValor(BigDecimal.ZERO);
        dto.setOrdem(ordem);
        return dto;
    }

    private OrcamentoDTO convertToDto(Orcamento orcamento) {
        OrcamentoDTO dto = modelMapper.map(orcamento, OrcamentoDTO.class);
        List<OrcamentoItemDTO> itens = new ArrayList<>();
        if (orcamento.getItens() != null) {
            orcamento.getItens().stream()
                    .sorted(Comparator.comparingInt(OrcamentoItem::getOrdem).thenComparing(OrcamentoItem::getId, Comparator.nullsLast(Long::compareTo)))
                    .forEach(item -> {
                        OrcamentoItemDTO itemDto = new OrcamentoItemDTO();
                        itemDto.setId(item.getId());
                        itemDto.setSecao(item.getSecao());
                        itemDto.setDescricao(item.getDescricao());
                        itemDto.setValor(item.getValor());
                        itemDto.setOrdem(item.getOrdem());
                        itens.add(itemDto);
                    });
        }
        dto.setItens(itens);
        dto.setTotalEquipe(somaSecao(orcamento, SecaoOrcamentoItem.EQUIPE));
        dto.setTotalHospitalar(somaSecao(orcamento, SecaoOrcamentoItem.HOSPITALAR));
        dto.setTotalMaterial(somaSecao(orcamento, SecaoOrcamentoItem.MATERIAL));
        dto.setTotalGeral(dto.getTotalEquipe()
                .add(dto.getTotalHospitalar())
                .add(dto.getTotalMaterial()));
        return dto;
    }

    private BigDecimal somaSecao(Orcamento orcamento, SecaoOrcamentoItem secao) {
        if (orcamento.getItens() == null) {
            return BigDecimal.ZERO;
        }
        return orcamento.getItens().stream()
                .filter(i -> i.getSecao() == secao)
                .map(i -> i.getValor() != null ? i.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
