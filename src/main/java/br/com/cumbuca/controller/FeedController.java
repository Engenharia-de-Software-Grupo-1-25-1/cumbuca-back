package br.com.cumbuca.controller;

import br.com.cumbuca.dto.feed.AvaliacaoFeedResponseDTO;
import br.com.cumbuca.dto.feed.TagPopularResponseDTO;
import br.com.cumbuca.service.feed.FeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/avaliacoes")
    public ResponseEntity<List<AvaliacaoFeedResponseDTO>> listarAvaliacoes() {
        final List<AvaliacaoFeedResponseDTO> avaliacoes = feedService.listarAvaliacoes();
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/tags-populares")
    public ResponseEntity<List<TagPopularResponseDTO>> listarTagsPopulares() {
        final List<TagPopularResponseDTO> tagsPopulares = feedService.listarTagsPopulares();
        return ResponseEntity.ok(tagsPopulares);
    }
}
