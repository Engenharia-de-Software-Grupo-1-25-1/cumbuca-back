package br.com.cumbuca.controller;

import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.service.comentario.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class ComentarioController {

    private ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }


    @PostMapping("avaliacao/comentar/{avaliacaoId}")
    public ResponseEntity<ComentarioResponseDTO> comentar(@PathVariable Long avaliacaoId,
                                                          @Valid @RequestBody String texto) {
        final ComentarioResponseDTO comentario = comentarioService.comentar(avaliacaoId, texto);
        return ResponseEntity.ok(comentario);
    }

    @DeleteMapping("comentario/remover/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        comentarioService.remover(id);
        return ResponseEntity.noContent().build();
    }

}
