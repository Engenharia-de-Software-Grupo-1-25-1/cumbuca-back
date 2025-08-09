package br.com.cumbuca.service.tag;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;

import java.util.List;

public interface TagService {
    List<Tag> criarTags(List<String> nomes, Avaliacao avaliacao);
}
