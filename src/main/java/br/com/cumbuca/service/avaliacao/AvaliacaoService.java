package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.avaliacao.FiltrarAvaliacaoRequestDTO;
import br.com.cumbuca.model.Avaliacao;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoService {
    Avaliacao criar(AvaliacaoRequestDTO avaliacaoRequestDTO);
    Avaliacao atualizar(Long id, AvaliacaoRequestDTO dto);
    void remover(Long id);
    List<AvaliacaoResponseDTO> filtrar(FiltrarAvaliacaoRequestDTO filtrarAvaliacaoRequestDTO);
}
