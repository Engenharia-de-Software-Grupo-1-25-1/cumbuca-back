package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.model.Estabelecimento;

public interface EstabelecimentoService {

    Estabelecimento buscarOuCriar(EstabelecimentoRequestDTO estabelecimentoRequestDTO);
}
