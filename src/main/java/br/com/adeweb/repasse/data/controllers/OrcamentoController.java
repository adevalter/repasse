package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.OrcamentoDTO;
import br.com.adeweb.repasse.domain.services.OrcamentoService;
import br.com.adeweb.repasse.domain.services.PdfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orcamento")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;
    private final PdfService pdfService;

    public OrcamentoController(OrcamentoService orcamentoService, PdfService pdfService) {
        this.orcamentoService = orcamentoService;
        this.pdfService = pdfService;
    }

    @GetMapping
    public ResponseEntity<Page<OrcamentoDTO>> getAll(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ) {
        return ResponseEntity.ok(orcamentoService.findAll(PageRequest.of(pageNumber, size)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrcamentoDTO save(@RequestBody OrcamentoDTO orcamentoDTO) {
        return orcamentoService.salvarOrcamento(orcamentoDTO);
    }

    @PutMapping("/{id}")
    public OrcamentoDTO update(@PathVariable Long id, @RequestBody OrcamentoDTO orcamentoDTO) {
        return orcamentoService.atualizarOrcamento(id, orcamentoDTO);
    }

    @GetMapping("/{id}")
    public OrcamentoDTO getById(@PathVariable Long id) {
        return orcamentoService.buscaPorid(id);
    }

    @GetMapping("/token/{chave}")
    public OrcamentoDTO getByChave(@PathVariable String chave) {
        return orcamentoService.buscaProAprovado(chave);
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<OrcamentoDTO> aprovar(@PathVariable Long id, @RequestParam Long aprovadorId) {
        return ResponseEntity.ok(orcamentoService.aprovarOrcamento(id, aprovadorId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam int status, @RequestParam Long aprovadorId) {
        try {
            return ResponseEntity.ok(orcamentoService.updateStatusOrcamento(id, status, aprovadorId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id,
                                              @RequestParam(required = false, defaultValue = "false") boolean email) {
        try {
            byte[] pdfBytes = pdfService.enviarOrcamento(id, email);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamento_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
