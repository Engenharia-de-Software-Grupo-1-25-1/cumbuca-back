package br.com.cumbuca.model.favorito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
class FavoritoId implements Serializable {
    private Long usuario;
    private Long estabelecimento;
}
