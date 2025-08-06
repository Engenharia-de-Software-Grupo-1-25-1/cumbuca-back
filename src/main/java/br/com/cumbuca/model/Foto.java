package br.com.cumbuca.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "FOTO")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AVALIACAO", nullable = false)
    private Avaliacao avaliacao;

    @Column(name = "FOTO")
    private byte[] foto;

}