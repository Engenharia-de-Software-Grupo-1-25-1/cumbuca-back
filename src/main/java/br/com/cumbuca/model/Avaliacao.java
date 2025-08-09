package br.com.cumbuca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "AVALIACAO")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

//    @ManyToOne
//    @JoinColumn(name = "ID_ESTABELECIMENTO", nullable = false)
//    private Estabelecimento estabelecimento;

    @Column(name = "CONSUMO", length = 50)
    private String consumo;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name = "PRECO")
    private BigDecimal preco;

    @Column(name = "NOTA_GERAL", nullable = false)
    private Integer notaGeral;

    @Column(name = "NOTA_COMIDA")
    private Integer notaComida;

    @Column(name = "NOTA_ATENDIMENTO")
    private Integer notaAtendimento;

    @Column(name = "NOTA_AMBIENTE")
    private Integer notaAmbiente;

    @Column(name = "DATA", nullable = false)
    private LocalDate data;
}