package br.com.cumbuca.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class UsuarioFavoritaEstabelecimentoId implements Serializable {

    private Long idUsuario;
    private Long idEstabelecimento;

    public UsuarioFavoritaEstabelecimentoId(Long idUsuario, Long idEstabelecimento) {
        this.idUsuario = idUsuario;
        this.idEstabelecimento = idEstabelecimento;
    }
}