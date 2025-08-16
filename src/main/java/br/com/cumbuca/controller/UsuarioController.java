package br.com.cumbuca.controller;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/criar")
    public ResponseEntity<UsuarioResponseDTO> criar(@ModelAttribute @Valid UsuarioRequestDTO usuarioRequestDTO,
                                                    UriComponentsBuilder uriBuilder) {
        final Usuario usuario = usuarioService.criar(usuarioRequestDTO);
        final URI uri = uriBuilder
                .path("/usuario/{username}")
                .buildAndExpand(usuario.getUsername())
                .toUri();

        return ResponseEntity.created(uri).body(new UsuarioResponseDTO(usuario));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @ModelAttribute @Valid UsuarioRequestDTO usuarioRequestDTO) {
        final Usuario usuario = usuarioService.atualizar(id, usuarioRequestDTO);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
    }

    @DeleteMapping("/remover/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        usuarioService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recuperar/{id}")
    public ResponseEntity<UsuarioResponseDTO> recuperar(@PathVariable Long id) {
        final Usuario usuario = usuarioService.recuperar(id);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
    }

}
