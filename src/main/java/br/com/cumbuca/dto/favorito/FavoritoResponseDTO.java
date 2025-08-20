package br.com.cumbuca.dto.favorito;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoritoResponseDTO {
    private boolean isFavorito;
    private String mensagem;

    public FavoritoResponseDTO(boolean isFavorito, String mensagem) {
        this.isFavorito = isFavorito;
        this.mensagem = mensagem;
    }
}