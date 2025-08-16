package br.com.cumbuca.dto.feed;


import lombok.Data;

@Data
public class TagPopularResponseDTO {
    private String nome;
    private Long quantidade;

    public TagPopularResponseDTO(String nome, Long quantidade) {
        this.nome = nome;
        this.quantidade = quantidade;
    }
}
