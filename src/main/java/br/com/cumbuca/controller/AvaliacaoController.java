package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.service.avaliacao.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.util.UriComponentsBuilder;

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
            @RequestParam(required = false) Long idEstabelecimento) {
        final List<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listar(idUsuario, idEstabelecimento);
        return ResponseEntity.ok(avaliacoes);
    }


    @PostMapping("/curtir/{id}/")
    public ResponseEntity<AvaliacaoResponseDTO> curtir(@PathVariable Long id) {
        final AvaliacaoResponseDTO avalicaoCurtida = avaliacaoService.curtir(id);
        return ResponseEntity.ok(avalicaoCurtida);
    }
}
