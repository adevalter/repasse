package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.RepasseItemDTO;
import br.com.adeweb.repasse.domain.repositories.RepasseRepository;
import br.com.adeweb.repasse.domain.services.RepasseItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/repasseitem")
public class RepasseItemController {

    @Autowired
    private RepasseItemService repasseItemService;

    @GetMapping
    public ResponseEntity<Page<RepasseItemDTO>> getAll(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ){
        return ResponseEntity.ok(repasseItemService.findAll(PageRequest.of(pageNumber,size)));
    }

    @GetMapping("/{id}")
    public RepasseItemDTO getByID(@PathVariable Long id){
        return repasseItemService.buscarPorId(id);
    }
    @GetMapping("/repasse/{id}")
    public List<RepasseItemDTO> repasseGetByID(@PathVariable Long id){
        return repasseItemService.buscarRepassePorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RepasseItemDTO save(@RequestBody RepasseItemDTO repasseItemDTO){
        return repasseItemService.salvarRepasseItem(repasseItemDTO);
    }

    @PutMapping("/{id}")
    public RepasseItemDTO update(@PathVariable Long id, @RequestBody RepasseItemDTO repasseItemDTO){
        RepasseItemDTO repasseItemAtual = repasseItemService.buscarPorId(id);
        BeanUtils.copyProperties(repasseItemDTO, repasseItemAtual, "id");
        return repasseItemService.salvarRepasseItem(repasseItemAtual);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam int status) {
        boolean updated = repasseItemService.updateStatus(id, status);

        if (updated) {
            return ResponseEntity.ok("Status atualizado com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Falha ao atualizar o status.");
        }
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id){
        repasseItemService.excluir(id);
    }

}
