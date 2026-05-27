package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.PagamentoDTO;
import br.com.adeweb.repasse.domain.services.PagamentoService;
import br.com.adeweb.repasse.domain.services.PdfService;
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
@RequestMapping("/pagamentos")
public class PagamentoController {
    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private RepasseService repasseService;

    @Autowired
    private PdfService pdfService;

    @GetMapping
    public ResponseEntity<Page<PagamentoDTO>> getAll(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ){
      return ResponseEntity.ok(pagamentoService.findAll(PageRequest.of(pageNumber,size)));
    }

    @GetMapping("/{id}")
    public PagamentoDTO getById(@PathVariable Long id){
        return pagamentoService.buscarById(id);
    }

    @GetMapping("/pesquisa")
    public ResponseEntity<Page<PagamentoDTO>> getByName(
            @RequestParam(required = false, defaultValue = "") String nome,
            @RequestParam(defaultValue = "1") Long tipoPessoa_id,
            @RequestParam(defaultValue = "0") final Integer pagNumber,
            @RequestParam(defaultValue = "10") final Integer size
    ){
        return ResponseEntity.ok(pagamentoService.findByName(nome, tipoPessoa_id, PageRequest.of(pagNumber,size)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public  PagamentoDTO save(@RequestBody PagamentoDTO pagamentoDTO){
        pagamentoDTO.setStatus(1);
        return pagamentoService.salvarPagamento(pagamentoDTO);
    }

    @PutMapping("/{id}")
    public PagamentoDTO update(@PathVariable Long id, @RequestBody PagamentoDTO pagamentoDTO){
        PagamentoDTO pagamentoAtual = pagamentoService.buscarById(id);
        pagamentoDTO.setStatus(2);
        BeanUtils.copyProperties(pagamentoDTO, pagamentoAtual,"id","total","UserResumoDTO","RepasseResumoDTO");
        return pagamentoService.salvarPagamento(pagamentoAtual);
    }
    @GetMapping("sendEmail/{id}")
    public String sendEmail(@PathVariable Long id){

        return  pdfService.sendEmailAviso(id);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id,  @RequestParam boolean enviarEmail) {
        try {
            // Gera o PDF e obtém os dados como byte[]
            byte[] pdfBytes = pdfService.sendEmailPagamento(id,enviarEmail);

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
    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id, @RequestParam Long repasseId){

        pagamentoService.excluir(id,repasseId);
    }
}
