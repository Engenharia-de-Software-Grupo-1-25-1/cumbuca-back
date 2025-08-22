package br.com.cumbuca.service.tag;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void criar(List<String> tags, Avaliacao avaliacao) {
        tags.stream()
                .filter(t -> !t.isEmpty())
                .forEach(t -> {
                    final Tag tag = new Tag();
                    tag.setTag(t);
                    tag.setAvaliacao(avaliacao);
                    tagRepository.save(tag);
                });
    }

    @Override
    public List<String> recuperar(Long avaliacaoId) {
        return tagRepository.findByAvaliacaoId(avaliacaoId)
                .stream()
                .map(Tag::getTag)
                .toList();
    }

        @Override
    public void remover(Long avaliacaoId) {
        final List<Tag> tags = tagRepository.findByAvaliacaoId(avaliacaoId);
        tagRepository.deleteAll(tags);
    }

}
