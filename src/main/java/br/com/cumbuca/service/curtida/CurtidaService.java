package br.com.cumbuca.service.curtida;

import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;

public interface CurtidaService {

    Integer qtdCurtidas(Long idAvaliacao);

    CurtidaResponseDTO curtir(Long id);
}
