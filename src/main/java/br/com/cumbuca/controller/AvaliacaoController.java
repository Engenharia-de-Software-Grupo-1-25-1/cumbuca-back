package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoAtualizacaoRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.service.avaliacao.AvaliacaoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    private final AvaliacaoServiceImpl avaliacaoServiceImpl;

    public AvaliacaoController(AvaliacaoServiceImpl avaliacaoServiceImpl) {
        this.avaliacaoServiceImpl = avaliacaoServiceImpl;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> atualizar(@PathVariable Long id, @RequestBody @Valid AvaliacaoAtualizacaoRequestDTO dto) {
        final Avaliacao avaliacaoAtualizada = avaliacaoServiceImpl.atualizar(id, dto);
        return ResponseEntity.ok(avaliacaoAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        avaliacaoServiceImpl.remover(id);
        return ResponseEntity.noContent().build();
    }
}