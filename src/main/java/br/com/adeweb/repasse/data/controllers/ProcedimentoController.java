package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.ProcedimentoDTO;
import br.com.adeweb.repasse.domain.services.ProcecimentoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/procedimentos")
public class ProcedimentoController {
    @Autowired
    private ProcecimentoService procecimentoService;

    @GetMapping
    public ResponseEntity<Page<ProcedimentoDTO>> getAll(
           @RequestParam(defaultValue = "0") final Integer pageMNumber,
           @RequestParam(defaultValue = "10") final Integer size
    ){
        return ResponseEntity.ok(procecimentoService.findAll(PageRequest.of(pageMNumber, size)));
    }

    @GetMapping("/{id}")
    public ProcedimentoDTO getById(@PathVariable Long id){
        return procecimentoService.buscaPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcedimentoDTO save(@RequestBody ProcedimentoDTO procedimentoDTO){
        return procecimentoService.salvarProcedimento(procedimentoDTO);
    }

    @PutMapping("/{id}")
    public ProcedimentoDTO update(@PathVariable Long id, @RequestBody ProcedimentoDTO procedimentoDTO){
        ProcedimentoDTO procedimentoAtual = procecimentoService.buscaPorId(id);
        BeanUtils.copyProperties(procedimentoDTO, procedimentoAtual,"id");
        return procecimentoService.salvarProcedimento(procedimentoAtual);
    }
}
