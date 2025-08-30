package br.com.cumbuca.service.comentario;

import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;

import java.util.List;

public interface ComentarioService {

    List<ComentarioResponseDTO> recuperar(Long avaliacaoId);

    ComentarioResponseDTO comentar(Long avaliacaoId, String texto);

    void remover(Long id);
}
