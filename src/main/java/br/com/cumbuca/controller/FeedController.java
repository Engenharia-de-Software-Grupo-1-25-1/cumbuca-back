package br.com.cumbuca.controller;

import br.com.cumbuca.dto.feed.AvaliacaoResumidaDTO;
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

    @GetMapping
    public ResponseEntity<List<AvaliacaoResumidaDTO>> getFeed() {
        List<AvaliacaoResumidaDTO> avaliacoes = feedService.getAvaliacoes();
        return ResponseEntity.ok(avaliacoes);
    }
}
