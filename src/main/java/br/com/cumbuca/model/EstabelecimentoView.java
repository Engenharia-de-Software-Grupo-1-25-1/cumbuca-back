package br.com.cumbuca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Data
@Entity
@Immutable
@Subselect("SELECT * FROM V_ESTABELECIMENTO")
public class EstabelecimentoView {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "CATEGORIA", nullable = false, length = 50)
    private String categoria;

    @Column(name = "RUA", length = 100)
    private String rua;

    @Column(name = "NUMERO", length = 20)
    private String numero;

    @Column(name = "BAIRRO", length = 50)
    private String bairro;

    @Column(name = "CIDADE", length = 50)
    private String cidade;

    @Column(name = "ESTADO", length = 2)
    private String estado;

    @Column(name = "CEP", length = 10)
    private String cep;

    @Column(name = "AVALIACOES")
    private Integer qtdAvaliacoes;

    @Column(name = "FAVORITADO")
    private Boolean favoritado;

    @Column(name = "NOTA_GERAL")
    private Double notaGeral;

}
