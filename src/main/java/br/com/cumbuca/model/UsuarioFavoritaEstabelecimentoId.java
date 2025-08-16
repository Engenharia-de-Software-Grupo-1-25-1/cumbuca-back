package br.com.cumbuca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioFavoritaEstabelecimentoId implements Serializable {

    @Column(name = "ID_USUARIO")
    private Long usuarioId;

    @Column(name = "ID_ESTABELECIMENTO")
    private Long estabelecimentoId;

}