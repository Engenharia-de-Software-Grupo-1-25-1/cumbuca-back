package br.com.cumbuca.dto.estabelecimento;

import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.EstabelecimentoView;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EstabelecimentoResponseDTO {

    private Long id;
    private String nome;
    private String categoria;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private List<String> horarios;
    private int qtdAvaliacoes;
    private Double notaGeral;
    private boolean isFavoritado;

    public EstabelecimentoResponseDTO(Estabelecimento estabelecimento) {
        this.id = estabelecimento.getId();
        this.nome = estabelecimento.getNome();
        this.categoria = estabelecimento.getCategoria();
        this.rua = estabelecimento.getRua();
        this.numero = estabelecimento.getNumero();
        this.bairro = estabelecimento.getBairro();
        this.cidade = estabelecimento.getCidade();
        this.estado = estabelecimento.getEstado();
        this.isFavoritado = false;
    }

    public EstabelecimentoResponseDTO(EstabelecimentoView estabelecimento) {
        this.id = estabelecimento.getId();
        this.nome = estabelecimento.getNome();
        this.categoria = estabelecimento.getCategoria();
        this.rua = estabelecimento.getRua();
        this.numero = estabelecimento.getNumero();
        this.bairro = estabelecimento.getBairro();
        this.cidade = estabelecimento.getCidade();
        this.estado = estabelecimento.getEstado();
        this.qtdAvaliacoes = estabelecimento.getQtdAvaliacoes();
        this.notaGeral = estabelecimento.getNotaGeral();
        this.isFavoritado = false;
    }
}

