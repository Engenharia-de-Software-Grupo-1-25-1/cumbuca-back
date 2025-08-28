package br.com.cumbuca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Subselect("SELECT * FROM V_AVALIACAO")
public class AvaliacaoView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTABELECIMENTO", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(name = "CONSUMO", length = 50, nullable = false)
    private String itemConsumido;

    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @DecimalMin(value = "0.0")
    @Column(name = "PRECO")
    private BigDecimal preco;

    @Column(name = "NOTA_GERAL")
    private Integer notaGeral;

    @Column(name = "NOTA_COMIDA")
    private Integer notaComida;

    @Column(name = "NOTA_ATENDIMENTO")
    private Integer notaAtendimento;

    @Column(name = "NOTA_AMBIENTE")
    private Integer notaAmbiente;

    @Column(name = "DATA")
    private LocalDate data =  LocalDate.now();

    @Column(name = "CURTIDAS")
    private Integer qtdCurtidas;

    @Column(name = "COMENTARIOS")
    private Integer qtdComentarios;
}
