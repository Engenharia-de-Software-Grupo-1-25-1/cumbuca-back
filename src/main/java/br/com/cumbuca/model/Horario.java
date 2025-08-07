package br.com.cumbuca.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table (name = "HORARIO")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTABELECIMENTO", nullable = false)
    private Estabelecimento estabelecimento;

    @Column (name = "HORARIO")
    private String horario;
}
