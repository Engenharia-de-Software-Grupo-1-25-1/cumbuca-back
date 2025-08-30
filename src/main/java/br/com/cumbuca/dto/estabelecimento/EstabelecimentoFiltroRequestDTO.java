package br.com.cumbuca.dto.estabelecimento;

import lombok.Data;

@Data
public class EstabelecimentoFiltroRequestDTO {
    private String nome;
    private String categoria;
    private String local;
    private boolean favoritado;
    private Double notaGeral;
}