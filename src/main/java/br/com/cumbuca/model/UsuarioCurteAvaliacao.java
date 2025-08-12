package br.com.cumbuca.model;

import jakarta.persistence.*;

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
