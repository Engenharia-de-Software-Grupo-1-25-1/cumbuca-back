package br.com.cumbuca.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "USUARIO_FAVORITA_ESTABELECIMENTO")
public class UsuarioFavoritaEstabelecimento {

    @EmbeddedId
    private UsuarioFavoritaEstabelecimentoId id;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @ManyToOne
    @MapsId("idEstabelecimento")
    @JoinColumn(name = "ID_ESTABELECIMENTO")
    private Estabelecimento estabelecimento;

    public UsuarioFavoritaEstabelecimento(Usuario usuario, Estabelecimento estabelecimento) {
        this.usuario = usuario;
        this.estabelecimento = estabelecimento;
        this.id = new UsuarioFavoritaEstabelecimentoId(usuario.getId(), estabelecimento.getId());
    }
}