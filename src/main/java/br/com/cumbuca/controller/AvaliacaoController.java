package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoFiltroRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;
import br.com.cumbuca.service.avaliacao.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestParam(required = false) Long idEstabelecimento,
            @ModelAttribute AvaliacaoFiltroRequestDTO filtros) {

        final List<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listar(idUsuario, idEstabelecimento, filtros);
        return ResponseEntity.ok(avaliacoes);
    }

    @PostMapping("/curtir/{id}")
    public ResponseEntity<CurtidaResponseDTO> curtir(@PathVariable Long id) {
        final CurtidaResponseDTO curtida = avaliacaoService.curtir(id);
        return ResponseEntity.ok(curtida);
    }

    @PostMapping("/comentar/{id}")
    public ResponseEntity<ComentarioResponseDTO> comentar(@PathVariable Long id,
        @Valid @RequestBody String texto) {
        final ComentarioResponseDTO comentario = avaliacaoService.comentar(id, texto);
        return ResponseEntity.ok(comentario);
    }

    @DeleteMapping("/removerComentario/{id}")
    public ResponseEntity<Void> removerComentario(@PathVariable Long id) {
        avaliacaoService.removerComentario(id);
        return ResponseEntity.noContent().build();
    }
}