package br.com.cumbuca.dto.estabelecimento;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Horario;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
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
    private int quantidadeAvaliacoes;
    private Double notaGeral;

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
        this.notaGeral = calculaNotaGeral(estabelecimento);
        this.quantidadeAvaliacoes = estabelecimento.getAvaliacoes().size();
        if (estabelecimento.getHorarios() != null) {
            this.horarios = estabelecimento.getHorarios().stream()
                    .map(Horario::getHorario)
                    .collect(Collectors.toList());
        } else {
            this.horarios = List.of();
        }
    }

    public Double calculaNotaGeral(Estabelecimento estabelecimento) {
        if (estabelecimento.getAvaliacoes() != null && !estabelecimento.getAvaliacoes().isEmpty()) {
            double somaDasNotas = estabelecimento.getAvaliacoes().stream()
                    .mapToDouble(Avaliacao::getNotaGeral)
                    .sum();

            return somaDasNotas / estabelecimento.getAvaliacoes().size();
        }

        return 0.0;
    }
}

