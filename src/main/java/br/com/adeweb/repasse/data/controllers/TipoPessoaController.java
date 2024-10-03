package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.TipoPessoaDTO;
import br.com.adeweb.repasse.domain.services.TipoPessoaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tipopessoas")
public class TipoPessoaController {
    @Autowired
    private TipoPessoaService tipoPessoaService;

    @GetMapping
    public ResponseEntity<Page<TipoPessoaDTO>> getAll(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ){
     return ResponseEntity.ok(tipoPessoaService.findAll(PageRequest.of(pageNumber,size)));
    }

    @GetMapping("/{id}")
    public TipoPessoaDTO byId(@PathVariable Long id) {
        return  tipoPessoaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoPessoaDTO save(@RequestBody TipoPessoaDTO tipoPessoaDTO){
        return tipoPessoaService.salvarTipoPessoa(tipoPessoaDTO);
    }

    @PutMapping("/{id}")
    public TipoPessoaDTO update(@PathVariable Long id, @RequestBody TipoPessoaDTO tipoPessoaDTO){
        TipoPessoaDTO tipoPessoaAtual = tipoPessoaService.buscarPorId(id);
        BeanUtils.copyProperties(tipoPessoaDTO, tipoPessoaAtual, "id");
        return  tipoPessoaService.salvarTipoPessoa(tipoPessoaDTO);
    }
}
