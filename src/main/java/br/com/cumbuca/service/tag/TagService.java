package br.com.cumbuca.service.tag;

import br.com.cumbuca.model.Avaliacao;

import java.util.List;

public interface TagService {
    void criar(List<String> tags, Avaliacao avaliacao);

    List<String> recuperar(Long avaliacaoId);
}
