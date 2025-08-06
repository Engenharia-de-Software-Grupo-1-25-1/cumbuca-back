package br.com.cumbuca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "AVALIACAO")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_ESTABELECIMENTO", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(name = "CONSUMO", length = 50)
    private String itemConsumido;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "PRECO")
    private BigDecimal preco;

    @Min(1)
    @Max(5)
    @Column(name = "NOTA_GERAL", nullable = false)
    private Integer notaGeral;

    @Min(1)
    @Max(5)
    @Column(name="NOTA_COMIDA")
    private Integer notaComida;

    @Min(1)
    @Max(5)
    @Column(name="NOTA_ATENDIMENTO")
    private Integer notaAtendimento;

    @Min(1)
    @Max(5)
    @Column(name="NOTA_AMBIENTE")
    private Integer notaAmbiente;

    @Column(name="DATA")
    private LocalDate data =  LocalDate.now();

    @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Foto> fotos;

    @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tag> tags;

}
