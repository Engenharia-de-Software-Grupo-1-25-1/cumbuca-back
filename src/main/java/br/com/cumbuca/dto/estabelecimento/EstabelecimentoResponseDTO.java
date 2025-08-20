package br.com.cumbuca.dto.estabelecimento;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Horario;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<AvaliacaoResponseDTO> avaliacoes;
    private List<String> tagsPopulares;
    private Boolean isFavorito;

    public EstabelecimentoResponseDTO(Estabelecimento estabelecimento) {
        this.id = estabelecimento.getId();
        this.nome = estabelecimento.getNome();
        this.categoria = estabelecimento.getCategoria();
        this.rua = estabelecimento.getRua();
        this.numero = estabelecimento.getNumero();
        this.bairro = estabelecimento.getBairro();
        this.cidade = estabelecimento.getCidade();
        this.estado = estabelecimento.getEstado();
        this.cep = estabelecimento.getCep();
        if (estabelecimento.getHorarios() != null) {
            this.horarios = estabelecimento.getHorarios().stream()
                    .map(Horario::getHorario)
                    .collect(Collectors.toList());
        } else {
            this.horarios = List.of();
        }
    }

    public EstabelecimentoResponseDTO(Estabelecimento estabelecimento,
                                      Double notaGeral,
                                      long quantidadeAvaliacoes,
                                      //BigDecimal precoMedioFinal,
                                      List<AvaliacaoResponseDTO> avaliacaoResponseDTOS
                                      //List<String> tagsPopulares,
                                      //boolean isFavorito
    ) {
        this.id = estabelecimento.getId();
        this.nome = estabelecimento.getNome();
        this.categoria = estabelecimento.getCategoria();
        this.rua = estabelecimento.getRua();
        this.numero = estabelecimento.getNumero();
        this.bairro = estabelecimento.getBairro();
        this.cidade = estabelecimento.getCidade();
        this.estado = estabelecimento.getEstado();
        this.cep = estabelecimento.getCep();
        if (estabelecimento.getHorarios() != null) {
            this.horarios = estabelecimento.getHorarios().stream()
                    .map(Horario::getHorario)
                    .collect(Collectors.toList());
        } else {
            this.horarios = List.of();
        }
        this.notaGeral = notaGeral;
        this.qtdAvaliacoes = (int) quantidadeAvaliacoes;
        this.avaliacoes = avaliacaoResponseDTOS;
//        this.tagsPopulares = tagsPopulares;
//        this.isFavorito = isFavorito;
    }
    }