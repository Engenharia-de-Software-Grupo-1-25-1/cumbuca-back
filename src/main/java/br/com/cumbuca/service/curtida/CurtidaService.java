package br.com.cumbuca.service.curtida;

import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;

public interface CurtidaService {

    CurtidaResponseDTO curtir(Long avaliacaoId);

    boolean isAvaliacaoCurtida(Long usuarioId, Long avaliacaoId);
}
