package br.com.cumbuca.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;

import lombok.Data;

@Data
@Entity
@IdClass(ComentarioId.class)
@Table(name = "USUARIO_COMENTA_AVALIACAO")
public class Comentario {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AVALIACAO", nullable = false)
    private Avaliacao avaliacao;

    @Column(name = "COMENTARIO", length = 255, nullable = false)
    private String comentario;


}