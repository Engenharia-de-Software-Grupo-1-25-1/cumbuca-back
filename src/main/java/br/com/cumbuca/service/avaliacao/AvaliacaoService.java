package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.model.Avaliacao;

public interface AvaliacaoService {
    Avaliacao criar(AvaliacaoRequestDTO avaliacaoRequestDTO);
}
