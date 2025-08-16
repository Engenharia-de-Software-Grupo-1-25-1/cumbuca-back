package br.com.cumbuca.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "USUARIO_FAVORITA_ESTABELECIMENTO")
public class UsuarioFavoritaEstabelecimento {

    @EmbeddedId
    private UsuarioFavoritaEstabelecimentoId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @ManyToOne
    @MapsId("estabelecimentoId")
    @JoinColumn(name = "ID_ESTABELECIMENTO")
    private Estabelecimento estabelecimento;

}