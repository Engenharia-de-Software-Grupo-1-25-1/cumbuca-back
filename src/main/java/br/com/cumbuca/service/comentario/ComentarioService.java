package br.com.cumbuca.service.comentario;

import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;

import java.util.List;

public interface ComentarioService {

    Integer qtdComentarios(Long idAvaliacao);

    List<ComentarioResponseDTO> recuperar(Long idAvaliacao);

    ComentarioResponseDTO comentar(Long id, String texto);

    void remover(Long id);
}
