package br.com.cumbuca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class UsuarioCurteAvaliacaoId implements Serializable {

    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "ID_AVALIACAO")
    private Long idAvaliacao;
}
