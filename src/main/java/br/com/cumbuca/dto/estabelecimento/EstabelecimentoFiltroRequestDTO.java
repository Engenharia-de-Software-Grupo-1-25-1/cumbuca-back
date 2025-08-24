package br.com.cumbuca.dto.estabelecimento;

import lombok.Data;

@Data
public class EstabelecimentoFiltroRequestDTO {
    private String nome;
    private String categoria;
    private String local;
    private String horarioInicio;
    private String horarioFim;
    private String favoritos;
    private Double notaGeral;
}