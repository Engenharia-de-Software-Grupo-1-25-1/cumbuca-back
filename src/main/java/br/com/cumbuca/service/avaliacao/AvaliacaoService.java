package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoAtualizacaoRequestDTO;
import br.com.cumbuca.model.Avaliacao;

public interface AvaliacaoService {
    Avaliacao atualizar(Long id, AvaliacaoAtualizacaoRequestDTO dto);

    void remover(Long id);
}