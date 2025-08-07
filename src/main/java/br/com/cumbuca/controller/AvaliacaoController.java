package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.service.avaliacao.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
}