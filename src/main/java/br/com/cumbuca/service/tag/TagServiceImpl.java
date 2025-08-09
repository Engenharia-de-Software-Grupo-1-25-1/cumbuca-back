package br.com.cumbuca.service.tag;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Override
    public List<Tag> criarTags(List<String> nomes, Avaliacao avaliacao) {
        return nomes.stream()
                .map(nome -> {
                    final Tag tag = new Tag();
                    tag.setTag(nome);
                    tag.setAvaliacao(avaliacao);
                    return tag;
                })
                .toList();
    }
}
