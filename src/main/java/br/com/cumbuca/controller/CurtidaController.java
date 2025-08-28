package br.com.cumbuca.controller;

import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;
import br.com.cumbuca.service.curtida.CurtidaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class CurtidaController {

    public CurtidaService curtidaService;

    public CurtidaController(CurtidaService curtidaService) {
        this.curtidaService = curtidaService;
    }

    @PostMapping("/avaliacao/curtir/{id}")
    public ResponseEntity<CurtidaResponseDTO> curtir(@PathVariable Long id) {
        final CurtidaResponseDTO curtida = curtidaService.curtir(id);
        return ResponseEntity.ok(curtida);
    }
}
