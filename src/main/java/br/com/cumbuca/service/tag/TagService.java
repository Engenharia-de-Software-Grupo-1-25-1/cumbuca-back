package br.com.cumbuca.service.tag;

import br.com.cumbuca.dto.tag.TagResponseDTO;
import br.com.cumbuca.model.Avaliacao;

import java.util.List;

public interface TagService {
    void criar(List<String> tags, Avaliacao avaliacao);

    List<String> recuperar(Long avaliacaoId);

    void remover(Long avaliacaoId);

    List<TagResponseDTO> listar();

    List<TagResponseDTO> listarTagsPopulares();
}
