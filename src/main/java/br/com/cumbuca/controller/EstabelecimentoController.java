package br.com.cumbuca.controller;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.service.estabelecimento.EstabelecimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estabelecimento")
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(EstabelecimentoService estabelecimentoService) {
        this.estabelecimentoService = estabelecimentoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<EstabelecimentoResponseDTO>> listar() {
        final List<EstabelecimentoResponseDTO> estabelecimentos = estabelecimentoService.listar();
        return ResponseEntity.ok(estabelecimentos);
    }

    @GetMapping("/detalhes/{id}")
    public ResponseEntity<EstabelecimentoResponseDTO> buscarDetalhesEstabelecimento(@PathVariable Long id) {
        final EstabelecimentoResponseDTO detalhes = estabelecimentoService.buscarDetalhesEstabelecimento(id);
        return ResponseEntity.ok(detalhes);
    }
}
