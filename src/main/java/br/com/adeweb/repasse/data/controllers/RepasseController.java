package br.com.adeweb.repasse.data.controllers;


import br.com.adeweb.repasse.data.models.RepasseDTO;
import br.com.adeweb.repasse.domain.services.RepasseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PutMapping("/{id}/status")
    public Boolean updateStatus(@PathVariable Long id, @RequestParam int status){
            return repasseService.updateStatus(id,status);
    }

    @PutMapping("/{id}")
    public RepasseDTO update(@PathVariable Long id, @RequestBody RepasseDTO repasseDTO){
        RepasseDTO repasseAtual = repasseService.buscarPorId(id);
        BeanUtils.copyProperties(repasseDTO, repasseAtual,"id");
        return repasseService.salvarRepasse(repasseAtual);
    }

    @GetMapping("/pesquisa")
    public ResponseEntity<Page<RepasseDTO>> getByName(
            @RequestParam(required = false, defaultValue = "") String nome,
            @RequestParam(defaultValue = "1") Long tipoPessoa_id,
            @RequestParam(defaultValue = "0") final Integer pagNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ){
        return ResponseEntity.ok(repasseService.findByName(nome, tipoPessoa_id, PageRequest.of(pagNumber,size)));
    }

    @PutMapping("/finalizar/{id}/status")
    public  ResponseEntity<byte[]> finalizarRepasse(@PathVariable Long id, @RequestParam int status){
        try {
            // Gera o PDF e obtém os dados como byte[]
            byte[] pdfBytes = repasseService.finalizarComPdf(id,status);

            // Retorna o PDF no corpo da resposta
            //Content-Disposition: attachment; filename="arquivo.pdf"
            //Content-Type: application/pdf
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=repasse_medico_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
