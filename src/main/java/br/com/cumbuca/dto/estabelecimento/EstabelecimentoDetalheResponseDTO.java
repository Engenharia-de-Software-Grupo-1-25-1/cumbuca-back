package br.com.cumbuca.dto.estabelecimento;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.model.Estabelecimento;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EstabelecimentoDetalheResponseDTO {

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
    private String foto;
    private Double notaGeralMedia;
    private Long quantidadeAvaliacoes;
    private BigDecimal precoMedio;
    private List<AvaliacaoResponseDTO> avaliacoes;
    private List<String> tagsPopulares;
    private Boolean isFavorito;

    public EstabelecimentoDetalheResponseDTO(Estabelecimento estabelecimento, Double notaGeralMedia, Long quantidadeAvaliacoes, BigDecimal precoMedio, List<AvaliacaoResponseDTO> avaliacoes, List<String> tagsPopulares, Boolean isFavorito) {
        this.id = estabelecimento.getId();
        this.nome = estabelecimento.getNome();
        this.categoria = estabelecimento.getCategoria();
        this.rua = estabelecimento.getRua();
        this.numero = estabelecimento.getNumero();
        this.bairro = estabelecimento.getBairro();
        this.cidade = estabelecimento.getCidade();
        this.estado = estabelecimento.getEstado();
        this.cep = estabelecimento.getCep();
        this.horarios = estabelecimento.getHorarios().stream()
                .map(horario -> horario.getHorario())
                .toList();
        // A foto do estabelecimento não está no modelo, é uma feature futura.
        this.foto = null;
        this.notaGeralMedia = notaGeralMedia;
        this.quantidadeAvaliacoes = quantidadeAvaliacoes;
        this.precoMedio = precoMedio;
        this.avaliacoes = avaliacoes;
        this.tagsPopulares = tagsPopulares;
        this.isFavorito = isFavorito;
    }
}