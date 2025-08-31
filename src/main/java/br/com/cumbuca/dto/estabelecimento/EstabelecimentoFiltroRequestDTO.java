package br.com.cumbuca.dto.estabelecimento;

import lombok.Data;

@Data
public class EstabelecimentoFiltroRequestDTO {
    private String nome;
    private String categoria;
    private String localizacao;
    private Boolean isFavoritado;
    private Double notaGeral;
}