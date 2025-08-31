package br.com.cumbuca.dto.Favorito;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Favorito;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoritoResponseDTO {

    private UsuarioResponseDTO usuario;
    private EstabelecimentoResponseDTO estabelecimento;
    private Boolean isFavoritado;

    public FavoritoResponseDTO(Favorito favorito) {
        this.usuario = new UsuarioResponseDTO(favorito.getUsuario());
        this.estabelecimento = new EstabelecimentoResponseDTO(favorito.getEstabelecimento());
    }
}
