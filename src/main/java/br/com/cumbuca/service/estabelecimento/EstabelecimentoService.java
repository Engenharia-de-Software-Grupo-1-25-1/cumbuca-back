package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoDetalheResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResumoResponseDTO;
import br.com.cumbuca.model.Estabelecimento;
import java.util.List;

public interface EstabelecimentoService {

    Estabelecimento buscarOuCriar(EstabelecimentoRequestDTO estabelecimentoRequestDTO);
    List<EstabelecimentoResumoResponseDTO> listarEstabelecimentosResumidos();
    EstabelecimentoDetalheResponseDTO buscarDetalhesEstabelecimento(Long id);
}