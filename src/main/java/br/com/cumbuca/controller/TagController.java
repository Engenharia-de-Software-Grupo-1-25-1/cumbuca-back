package br.com.cumbuca.controller;

import br.com.cumbuca.dto.tag.TagResponseDTO;
import br.com.cumbuca.service.tag.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<TagResponseDTO>> listar() {
        final List<TagResponseDTO> tags = tagService.listar();
        return ResponseEntity.ok(tags);

    }

    @GetMapping("/populares/listar")
    public ResponseEntity<List<TagResponseDTO>> listarTagsPopulares() {
        final List<TagResponseDTO> tags = tagService.listarTagsPopulares();
        return ResponseEntity.ok(tags);

    }
}
