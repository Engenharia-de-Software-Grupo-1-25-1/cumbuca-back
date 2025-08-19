package br.com.cumbuca.service.tag;

import br.com.cumbuca.dto.tag.TagResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.TagUtils;

import java.text.Normalizer;
import java.util.*;
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

    public static String normalizar(String tag) {
        if (tag == null) return null;
        String semAcento = Normalizer.normalize(tag, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.trim().toLowerCase();
    }


    @Override
    public List<TagResponseDTO> listar() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> {
                    TagResponseDTO dto = modelMapper.map(tag, TagResponseDTO.class);
                    dto.setTag(normalizar(tag.getTag()));
                    dto.setQuantidade(null);
                    return dto;
                })
                .toList();
    }

    @Override
    public List<TagResponseDTO> listarTagsPopulares() {
        List<Tag> tags = tagRepository.findAll();

        return tags.stream()
                .collect(Collectors.groupingBy(
                        tag -> normalizar(tag.getTag()),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> {
                    TagResponseDTO dto = new TagResponseDTO();
                    dto.setId(null);
                    dto.setTag(entry.getKey());
                    dto.setQuantidade(entry.getValue().intValue());
                    return dto;
                })
                .sorted(Comparator.comparingInt(TagResponseDTO::getQuantidade).reversed())
                .limit(5)
                .toList();
    }

}


