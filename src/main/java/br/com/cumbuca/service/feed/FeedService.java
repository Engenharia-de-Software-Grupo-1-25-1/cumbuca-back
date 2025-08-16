package br.com.cumbuca.service.feed;

import java.util.List;

import br.com.cumbuca.dto.feed.AvaliacaoFeedResponseDTO;
import br.com.cumbuca.dto.feed.TagPopularResponseDTO;

public interface FeedService {

    List<AvaliacaoFeedResponseDTO> listarAvaliacoes();
    
    List<TagPopularResponseDTO> listarTagsPopulares();
}
