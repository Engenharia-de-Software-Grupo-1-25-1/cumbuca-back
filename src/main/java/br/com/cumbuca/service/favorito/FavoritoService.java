package br.com.cumbuca.service.favorito;

import br.com.cumbuca.dto.favorito.FavoritoResponseDTO;

public interface FavoritoService {
    FavoritoResponseDTO favoritar(Long idEstabelecimento);
}
