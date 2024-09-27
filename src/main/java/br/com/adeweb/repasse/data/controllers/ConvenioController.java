package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.ConvenioDTO;
import br.com.adeweb.repasse.data.models.EmpresaDTO;
import br.com.adeweb.repasse.domain.services.ConvenioService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/convenios")
public class ConvenioController {

    @Autowired
    private ConvenioService convenioService;

    @GetMapping
    public ResponseEntity<Page<ConvenioDTO>> getAll(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ){
        return ResponseEntity.ok(convenioService.findAll(PageRequest.of(pageNumber,size)));
    }

    @GetMapping("/{id}")
    public ConvenioDTO byId(@PathVariable Long id){
        return convenioService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConvenioDTO save(@RequestBody ConvenioDTO convenioDTO){
        return convenioService.salvarConvenio(convenioDTO);
    }

    @PutMapping("/{id}")
    public ConvenioDTO update(@PathVariable Long id, @RequestBody ConvenioDTO convenioDTO){
        ConvenioDTO convenioAtual = convenioService.buscarPorId(id);
        BeanUtils.copyProperties(convenioDTO, convenioAtual,"id");
        return convenioService.salvarConvenio(convenioAtual);

    }

}
