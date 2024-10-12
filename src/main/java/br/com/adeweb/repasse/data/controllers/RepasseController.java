package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.RepasseDTO;
import br.com.adeweb.repasse.domain.services.RepasseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repasse")
public class RepasseController {
    @Autowired
    private RepasseService repasseService;

    @GetMapping
    public ResponseEntity<Page<RepasseDTO>> getAll(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
            ){
        return ResponseEntity.ok(repasseService.findAll(PageRequest.of(pageNumber,size)));
    }
    @GetMapping("/{id}")
    public RepasseDTO getByid(@PathVariable Long id){
        return repasseService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RepasseDTO save(@RequestBody RepasseDTO repasseDTO){
        return repasseService.salvarRepasse(repasseDTO);
    }

    @PutMapping("/{id}")
    public RepasseDTO update(@PathVariable Long id, @RequestBody RepasseDTO repasseDTO){
        RepasseDTO repasseAtual = repasseService.buscarPorId(id);
        BeanUtils.copyProperties(repasseDTO, repasseAtual,"id");
        return repasseService.salvarRepasse(repasseAtual);
    }
}
