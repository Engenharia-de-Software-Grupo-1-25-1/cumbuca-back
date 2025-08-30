package br.com.cumbuca.dto.estabelecimento;

import lombok.Data;

@Data
public class EstabelecimentoFiltroRequestDTO {
    private String nome;
    private String categoria;
    private String localizacao;
    private boolean isFavoritado;
    private Double notaGeral;
}