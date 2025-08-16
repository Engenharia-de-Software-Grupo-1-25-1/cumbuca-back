package br.com.cumbuca.service.feed;

import br.com.cumbuca.dto.feed.AvaliacaoFeedResponseDTO;
import br.com.cumbuca.dto.feed.TagPopularResponseDTO;
import org.springframework.stereotype.Service;

import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.FotoRepository;
import br.com.cumbuca.repository.TagRepository;
import br.com.cumbuca.repository.UsuarioComentaAvaliacaoRepository;
import br.com.cumbuca.repository.UsuarioCurteAvaliacaoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioCurteAvaliacaoRepository usuarioCurteAvaliacaoRepository;
    private final UsuarioComentaAvaliacaoRepository usuarioComentaAvaliacaoRepository;
    private final FotoRepository fotoRepository;
    private final TagRepository tagRepository;

    public FeedServiceImpl(
            AvaliacaoRepository avaliacaoRepository,
            UsuarioCurteAvaliacaoRepository usuarioCurteAvaliacaoRepository,
            UsuarioComentaAvaliacaoRepository usuarioComentaAvaliacaoRepository,
            FotoRepository fotoRepository,
            TagRepository tagRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioCurteAvaliacaoRepository = usuarioCurteAvaliacaoRepository;
        this.usuarioComentaAvaliacaoRepository = usuarioComentaAvaliacaoRepository;
        this.fotoRepository = fotoRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvaliacaoFeedResponseDTO> listarAvaliacoes() {
        return avaliacaoRepository.findAllParaFeed().stream()
                .map(avaliacao -> new AvaliacaoFeedResponseDTO(
                        avaliacao,
                        usuarioCurteAvaliacaoRepository.countByAvaliacaoId(avaliacao.getId()),
                        usuarioComentaAvaliacaoRepository.countByAvaliacaoId(avaliacao.getId()),
                        fotoRepository.findByAvaliacaoId(avaliacao.getId()),
                        tagRepository.findByAvaliacaoId(avaliacao.getId())
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagPopularResponseDTO> listarTagsPopulares() {
        return tagRepository.findTop5TagsPopulares().stream()
                .map(result -> new TagPopularResponseDTO(
                        (String) result[0], 
                        ((Number) result[1]).longValue()
                ))
                .toList();
    }
}


