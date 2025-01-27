package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.PessoaDTO;
import br.com.adeweb.repasse.domain.services.PessoaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {
    @Autowired
    PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<Page<PessoaDTO>> getAll(
            @RequestParam(defaultValue = "0") final Integer pagNumber,
            @RequestParam(defaultValue = "10") final Integer size,
            @RequestParam Long tp
    ){
        return ResponseEntity.ok(pessoaService.findAllByTipoPessoa(PageRequest.of(pagNumber,size), tp));
    }

    @GetMapping("/pesquisa")
    public ResponseEntity<Page<PessoaDTO>> getByName(
            @RequestParam(required = false, defaultValue = "") String nome,
            @RequestParam(defaultValue = "1") Long tipoPessoa_id,
            @RequestParam(defaultValue = "0") final Integer pagNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ){
        return ResponseEntity.ok(pessoaService.findByName(nome, tipoPessoa_id, PageRequest.of(pagNumber,size)));
    }

    @GetMapping("/paciente/{id}")
    public PessoaDTO byId(@PathVariable Long id){
        return pessoaService.buscarPorId(id);
    }

    @GetMapping("/medico/{id}")
        public PessoaDTO medicoById(@PathVariable Long id){
        return pessoaService.buscarMedicoPorId(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaDTO save(@RequestBody PessoaDTO pessoaDTO){
        return pessoaService.salvarPessoa(pessoaDTO);
    }

    @PutMapping("/{id}")
    public PessoaDTO update(@PathVariable Long id, @RequestBody PessoaDTO pessoaDTO){
        PessoaDTO pessoaAtual = pessoaService.buscarPorId(id);
        BeanUtils.copyProperties(pessoaDTO, pessoaAtual, "id");
        return pessoaService.salvarPessoa(pessoaAtual);
    }

}
