package br.com.cumbuca.model;

import br.com.cumbuca.service.usuario.UsuarioService;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table (name = "USUARIO_CURTE_AVALIACAO")
public class UsuarioCurteAvaliacao {

    @EmbeddedId
    private UsuarioCurteAvaliacaoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idAvaliacao")
    @JoinColumn(name = "ID_AVALIACAO")
    private Avaliacao avaliacao;
}