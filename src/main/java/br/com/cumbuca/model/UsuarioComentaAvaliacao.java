package br.com.cumbuca.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table (name = "USUARIO_COMENTA_AVALIACAO")
public class UsuarioComentaAvaliacao {

    @EmbeddedId
    private UsuarioComentaAvaliacaoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idAvaliacao")
    @JoinColumn(name = "ID_AVALIACAO")
    private Avaliacao avaliacao;

    @Column(name = "COMENTARIO", length = 255, nullable = false)
    private String comentario;


}
