package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.Favorito.FavoritoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoFiltroRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.model.Estabelecimento;
import java.util.List;

public interface EstabelecimentoService {
    Estabelecimento buscarOuCriar(EstabelecimentoRequestDTO estabelecimentoRequestDTO);

    List<EstabelecimentoResponseDTO> listar(EstabelecimentoFiltroRequestDTO filtros, String ordenador);

    EstabelecimentoResponseDTO recuperar(Long id);

    FavoritoResponseDTO favoritar(Long id);
}