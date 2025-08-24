package br.com.cumbuca.controller;

import br.com.cumbuca.dto.Favorito.FavoritoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.service.estabelecimento.EstabelecimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/recuperar/{id}")
    public ResponseEntity<EstabelecimentoResponseDTO> recuperar(@PathVariable Long id) {
        final EstabelecimentoResponseDTO estabelecimento = estabelecimentoService.recuperar(id);
        return ResponseEntity.ok(estabelecimento);
    }

    @PostMapping("/favoritar/{id}")
    public ResponseEntity<FavoritoResponseDTO> favoritar(@PathVariable Long id) {
        final FavoritoResponseDTO favorito = estabelecimentoService.favoritar(id);
        return ResponseEntity.ok(favorito);
    }
}
