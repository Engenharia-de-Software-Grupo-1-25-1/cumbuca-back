package br.com.cumbuca.dto.feed;

import lombok.Data;

import java.util.List;

@Data
public class FeedResponseDTO {

    List<AvaliacaoResumidaDTO> avaliacoes;

    public FeedResponseDTO(List<AvaliacaoResumidaDTO> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }
}
