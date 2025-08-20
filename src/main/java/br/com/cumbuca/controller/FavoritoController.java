package br.com.cumbuca.controller;

import br.com.cumbuca.dto.favorito.FavoritoResponseDTO;
import br.com.cumbuca.service.favorito.FavoritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @PostMapping("/favoritar/{idEstabelecimento}")
    public ResponseEntity<FavoritoResponseDTO> favoritar(@PathVariable Long idEstabelecimento) {
        final FavoritoResponseDTO resposta = favoritoService.favoritar(idEstabelecimento);
        return ResponseEntity.ok(resposta);
    }
}