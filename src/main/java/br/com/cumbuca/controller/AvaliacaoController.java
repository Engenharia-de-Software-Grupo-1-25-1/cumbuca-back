package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.service.avaliacao.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) { this.avaliacaoService = avaliacaoService; }

    @PostMapping("/criar")
    public ResponseEntity<AvaliacaoResponseDTO> criar(
            @ModelAttribute @Valid AvaliacaoRequestDTO avaliacaoRequestDTO,
            UriComponentsBuilder uriBuilder) {
        final Avaliacao avaliacao = avaliacaoService.criar(avaliacaoRequestDTO);
        final URI uri = uriBuilder
                .path("/avaliacao/{id}")
                .buildAndExpand(avaliacao.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new AvaliacaoResponseDTO(avaliacao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> atualizar(@PathVariable Long id, @RequestBody @Valid AvaliacaoRequestDTO dto) {
        final Avaliacao avaliacaoAtualizada = avaliacaoService.atualizar(id, dto);
        return ResponseEntity.ok(avaliacaoAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        avaliacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
