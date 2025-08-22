package br.com.cumbuca.service.tag;

import br.com.cumbuca.dto.tag.TagResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    public TagServiceImpl(TagRepository tagRepository, ModelMapper modelMapper) {
        this.tagRepository = tagRepository;
        this.modelMapper = modelMapper;
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

    public static String normalizarTag(String tag) {
        if (tag == null) { return null; }
        final String semAcento = Normalizer.normalize(tag, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.trim().toLowerCase();
    }


    @Override
    public List<TagResponseDTO> listar() {
        final List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> {
                    final TagResponseDTO tagResponseDTO = new TagResponseDTO(tag);
                    tagResponseDTO.setTag(normalizarTag(tag.getTag()));
                    return tagResponseDTO;
                })
                .toList();
    }

    @Override
    public List<TagResponseDTO> listarTagsPopulares() {
        final List<Tag> tags = tagRepository.findAll();

        return tags.stream()
                .collect(Collectors.groupingBy(
                        tag -> normalizarTag(tag.getTag()),
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

}


