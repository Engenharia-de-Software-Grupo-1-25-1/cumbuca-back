package br.com.cumbuca.dto.estabelecimento;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EstabelecimentoResumoResponseDTO {
    private Long id;
    private String nome;
    private String categoria;
    private String localizacao;
    private Double notaGeral;
    private Long quantidadeAvaliacoes;
    private BigDecimal precoMedio;

    public EstabelecimentoResumoResponseDTO(
            Long id,
            String nome,
            String categoria,
            String rua,
            String numero,
            String bairro,
            String cidade,
            Double notaGeral,
            Long quantidadeAvaliacoes,
            BigDecimal precoMedio
    ) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.localizacao = String.format("%s, %s, %s, %s", rua, numero, bairro, cidade);
        this.notaGeral = notaGeral;
        this.quantidadeAvaliacoes = quantidadeAvaliacoes;
        this.precoMedio = precoMedio;
    }
}