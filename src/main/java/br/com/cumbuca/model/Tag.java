package br.com.cumbuca.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TAG")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AVALIACAO", nullable = false)
    private Avaliacao avaliacao;

    @Column(name = "TAG", nullable = false)
    private String tag;
}
