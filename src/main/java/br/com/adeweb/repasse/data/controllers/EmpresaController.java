package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.EmpresaDTO;
import br.com.adeweb.repasse.domain.services.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.adeweb.repasse.domain.repositories.EmpresaRepository;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("empresa")
public class EmpresaController {
    @Autowired
    private EmpresaService empresaService;

    private EmpresaRepository empresaRepository;


    @GetMapping()
    public ResponseEntity<Page<EmpresaDTO>> getAllEmpresa(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ) {
        return ResponseEntity.ok(empresaService.findAll(PageRequest.of(pageNumber,size)));
    }


    
}
