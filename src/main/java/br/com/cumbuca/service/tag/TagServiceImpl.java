package br.com.cumbuca.service.tag;

import br.com.cumbuca.dto.tag.TagResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;


    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void criar(List<String> tags, Avaliacao avaliacao) {
        tags.stream()
            .map(TagServiceImpl::normalizarTag)
            .filter(t -> !t.isEmpty())
            .distinct()
            .forEach(t -> {
                final Tag tag = new Tag();
                tag.setConteudo(t);
                tag.setAvaliacao(avaliacao);
                tagRepository.save(tag);
            });
    }

    @Override
    public List<String> recuperar(Long avaliacaoId) {
        return tagRepository.findByAvaliacaoId(avaliacaoId)
                .stream()
                .map(Tag::getConteudo)
                .toList();
    }

    @Override
    public void remover(Long avaliacaoId) {
        final List<Tag> tags = tagRepository.findByAvaliacaoId(avaliacaoId);
        tagRepository.deleteAll(tags);
    }

    @Override
    public List<TagResponseDTO> listar() {
        final List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> {
                    final TagResponseDTO tagResponseDTO = new TagResponseDTO(tag);
                    tagResponseDTO.setTag(normalizarTag(tag.getConteudo()));
                    return tagResponseDTO;
                })
                .toList();
    }

    @Override
    public List<TagResponseDTO> listarTagsPopulares() {
        final List<Tag> tags = tagRepository.findAll();

        return tags.stream()
                .collect(Collectors.groupingBy(
                        tag -> normalizarTag(tag.getConteudo()),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> {
                    final TagResponseDTO tagResponseDTO = new TagResponseDTO();
                    tagResponseDTO.setId(null);
                    tagResponseDTO.setTag(entry.getKey());
                    tagResponseDTO.setQuantidade(entry.getValue().intValue());
                    return tagResponseDTO;
                })
                .sorted(Comparator.comparingInt(TagResponseDTO::getQuantidade).reversed())
                .limit(5)
                .toList();
    }

    public static String normalizarTag(String tag) {
        if (tag == null) { return null; }
        final String semAcento = Normalizer.normalize(tag, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.trim().toLowerCase();
    }

}
