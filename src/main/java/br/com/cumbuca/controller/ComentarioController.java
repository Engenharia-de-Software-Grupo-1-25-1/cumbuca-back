package br.com.cumbuca.controller;

import br.com.cumbuca.service.comentario.ComentarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;


@RestController
@RequestMapping("/comentario")
public class ComentarioController {

    private ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @DeleteMapping("/remover/{id}")
    public ResponseEntity<Void> removerComentario(@PathVariable Long id) {
        comentarioService.removerComentario(id);
        return ResponseEntity.noContent().build();
    }

}
