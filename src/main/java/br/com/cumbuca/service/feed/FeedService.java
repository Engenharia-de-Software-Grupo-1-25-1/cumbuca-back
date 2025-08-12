package br.com.cumbuca.service.feed;

import java.util.List;

import br.com.cumbuca.dto.feed.AvaliacaoResumidaDTO;

public interface FeedService {

    List<AvaliacaoResumidaDTO> getAvaliacoes();
}
