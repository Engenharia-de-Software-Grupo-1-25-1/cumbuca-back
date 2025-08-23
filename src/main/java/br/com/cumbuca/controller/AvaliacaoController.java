package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.service.avaliacao.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping("/criar")
    public ResponseEntity<AvaliacaoResponseDTO> criar(
            @ModelAttribute @Valid AvaliacaoRequestDTO avaliacaoRequestDTO,
            UriComponentsBuilder uriBuilder) {
        final AvaliacaoResponseDTO avaliacao = avaliacaoService.criar(avaliacaoRequestDTO);
        final URI uri = uriBuilder
                .path("/avaliacao/{id}")
                .buildAndExpand(avaliacao.getId())
                .toUri();
        return ResponseEntity.created(uri).body(avaliacao);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> atualizar(@PathVariable Long id, @ModelAttribute @Valid AvaliacaoRequestDTO avaliacaoRequestDTO) {
        final AvaliacaoResponseDTO avaliacao = avaliacaoService.atualizar(id, avaliacaoRequestDTO);
        return ResponseEntity.ok(avaliacao);
    }

    @DeleteMapping("/remover/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        avaliacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recuperar/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> recuperar(@PathVariable Long id) {
        final AvaliacaoResponseDTO avaliacao = avaliacaoService.recuperar(id);
        return ResponseEntity.ok(avaliacao);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listar(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idEstabelecimento,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String estabelecimento,
            @RequestParam(required = false) String itemConsumido,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) BigDecimal preco,
            @RequestParam(required = false, defaultValue = "-1") int notaGeral,
            @RequestParam(required = false, defaultValue = "-1") int notaComida,
            @RequestParam(required = false, defaultValue = "-1") int notaAmbiente,
            @RequestParam(required = false, defaultValue = "-1") int notaAtendimento) {

        final List<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listar(
                idUsuario, idEstabelecimento, usuario, estabelecimento,
                itemConsumido, tags, preco, notaGeral, notaComida,
                notaAmbiente, notaAtendimento
        );
        return ResponseEntity.ok(avaliacoes);
    }

}
