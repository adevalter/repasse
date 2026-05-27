package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.ConvenioDTO;
import br.com.adeweb.repasse.data.models.DespesaMedicoDTO;
import br.com.adeweb.repasse.domain.services.DespesaMedicoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("despesamedico")
public class DespesaMedicoController {

    private final DespesaMedicoService despesaMedicoService;

    public DespesaMedicoController(DespesaMedicoService despesaMedicoService) {
        this.despesaMedicoService = despesaMedicoService;
    }

    @GetMapping
    public ResponseEntity<Page<DespesaMedicoDTO>> getAll(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ){
        return ResponseEntity.ok(despesaMedicoService.findAll(PageRequest.of(pageNumber,size)));
    }

    @GetMapping("/{id}")
    public DespesaMedicoDTO byId(@PathVariable Long id){
        return despesaMedicoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DespesaMedicoDTO save(@RequestBody DespesaMedicoDTO despesaMedicoDTO){
        return despesaMedicoService.salvar(despesaMedicoDTO);
    }

    @PutMapping("/{id}")
    public DespesaMedicoDTO update(@PathVariable Long id, @RequestBody DespesaMedicoDTO despesaMedicoDTO){
        return despesaMedicoService.atualizar(id,despesaMedicoDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover (@PathVariable Long id) {
        despesaMedicoService.excluir(id);
    }
}
