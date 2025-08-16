package br.com.cumbuca.controller;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoDetalheResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResumoResponseDTO;
import br.com.cumbuca.service.estabelecimento.EstabelecimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estabelecimentos")
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(EstabelecimentoService estabelecimentoService) {
        this.estabelecimentoService = estabelecimentoService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<List<EstabelecimentoResumoResponseDTO>> listarResumo() {
        final List<EstabelecimentoResumoResponseDTO> estabelecimentos = estabelecimentoService.listarEstabelecimentosResumidos();
        return ResponseEntity.ok(estabelecimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoDetalheResponseDTO> buscarDetalhesEstabelecimento(@PathVariable Long id) {
        final EstabelecimentoDetalheResponseDTO detalhes = estabelecimentoService.buscarDetalhesEstabelecimento(id);
        return ResponseEntity.ok(detalhes);
    }
}
