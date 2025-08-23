package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;

import java.util.List;

public interface AvaliacaoService {
    AvaliacaoResponseDTO criar(AvaliacaoRequestDTO avaliacaoRequestDTO);

    AvaliacaoResponseDTO atualizar(Long id, AvaliacaoRequestDTO dto);

    void remover(Long id);

    AvaliacaoResponseDTO recuperar(Long id);

    List<AvaliacaoResponseDTO> listar(Long idUsuario, Long idEstabelecimento);

    CurtidaResponseDTO curtir(Long id);

    ComentarioResponseDTO comentar(Long id, String comentario);

    void removerComentario(Long id, Long idComentario);
}
