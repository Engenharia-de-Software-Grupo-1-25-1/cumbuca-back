package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;

public interface AvaliacaoService {
    AvaliacaoResponseDTO criar(AvaliacaoRequestDTO avaliacaoRequestDTO);

    AvaliacaoResponseDTO atualizar(Long id, AvaliacaoRequestDTO dto);

    void remover(Long id);

    AvaliacaoResponseDTO recuperar(Long id);
}
