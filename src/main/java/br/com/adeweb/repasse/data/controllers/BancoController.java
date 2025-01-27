package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.BancoDTO;
import br.com.adeweb.repasse.domain.services.BancoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bancos")
public class BancoController {

    @Autowired
    private BancoService bancoService;

    @GetMapping
    public ResponseEntity<List<BancoDTO>> getAll(){
        return ResponseEntity.ok(bancoService.findAll());
    }
    @GetMapping("/{id}")
    public BancoDTO byId(@PathVariable Long id){
        return  bancoService.buscarById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BancoDTO save(@RequestBody BancoDTO bancoDTO){
        return bancoService.salvarBanco(bancoDTO);
    }

    @PutMapping("/{id}")
    public BancoDTO update(@PathVariable Long id, @RequestBody BancoDTO bancoDTO){
        BancoDTO bancoAtual = bancoService.buscarById(id);
        BeanUtils.copyProperties(bancoDTO, bancoAtual,"id");
        return bancoService.salvarBanco(bancoAtual);
    }

}
