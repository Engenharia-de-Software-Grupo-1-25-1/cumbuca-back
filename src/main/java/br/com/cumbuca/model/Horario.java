package br.com.cumbuca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
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
