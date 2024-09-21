package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.EmpresaDTO;
import br.com.adeweb.repasse.domain.entities.Empresa;
import br.com.adeweb.repasse.domain.services.EmpresaService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.adeweb.repasse.domain.repositories.EmpresaRepository;


@RestController
@RequestMapping("/empresas")
public class EmpresaController {
    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<Page<EmpresaDTO>> getAllEmpresa(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ) {
        return ResponseEntity.ok(empresaService.findAll(PageRequest.of(pageNumber,size)));
    }

    @GetMapping("/{id}")
    public EmpresaDTO byId(@PathVariable Long id){
        return empresaService.buscaPorId(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaDTO add(@RequestBody EmpresaDTO empresa){
        return empresaService.salvar(empresa);
    }

    @PutMapping("/{id}")
    public EmpresaDTO update(@PathVariable Long id, @RequestBody Empresa empresa){
        EmpresaDTO empresaAtual = empresaService.buscaPorId(id);
        empresa.setCreatedAt(empresaAtual.getCreatedAt());
        empresa.setUpdateAt(empresa.getUpdateAt());
        BeanUtils.copyProperties(empresa, empresaAtual, "id");
        return empresaService.salvar(empresaAtual);
    }

}
