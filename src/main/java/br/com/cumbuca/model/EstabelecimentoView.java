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

    @Column(name = "NOME")
    private String nome;

    @Column(name = "CATEGORIA")
    private String categoria;

    @Column(name = "RUA")
    private String rua;

    @Column(name = "NUMERO")
    private String numero;

    @Column(name = "BAIRRO")
    private String bairro;

    @Column(name = "CIDADE")
    private String cidade;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "CEP")
    private String cep;

    @Column(name = "LOCALIZACAO")
    private String localizacao;

    @Column(name = "AVALIACOES")
    private Integer qtdAvaliacoes;

    @Column(name = "FAVORITADO")
    private Boolean isFavoritado;

    @Column(name = "NOTA_GERAL")
    private Double notaGeral;

}
