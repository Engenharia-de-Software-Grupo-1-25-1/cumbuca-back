package br.com.cumbuca.service.feed;


import br.com.cumbuca.dto.feed.AvaliacaoResumidaDTO;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.UsuarioComentaAvaliacaoRepository;
import br.com.cumbuca.repository.UsuarioCurteAvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioCurteAvaliacaoRepository usuarioCurteAvaliacaoRepository;
    private final UsuarioComentaAvaliacaoRepository usuarioComentaAvaliacaoRepository;

    public FeedServiceImpl(
            AvaliacaoRepository avaliacaoRepository,
            UsuarioCurteAvaliacaoRepository usuarioCurteAvaliacaoRepository,
            UsuarioComentaAvaliacaoRepository usuarioComentaAvaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioCurteAvaliacaoRepository = usuarioCurteAvaliacaoRepository;
        this.usuarioComentaAvaliacaoRepository = usuarioComentaAvaliacaoRepository;
    }

    @Override
    public List<AvaliacaoResumidaDTO> getAvaliacoes() {
        return avaliacaoRepository.findAllByOrderByDataDesc()
                .stream()
                .map(avaliacao -> {
                    int qtdCurtidas = usuarioCurteAvaliacaoRepository.countByAvaliacaoId(avaliacao.getId());
                    int qtdComentarios = usuarioComentaAvaliacaoRepository.countByAvaliacaoId(avaliacao.getId());
                    return new AvaliacaoResumidaDTO(avaliacao, qtdCurtidas, qtdComentarios);
                })
                .toList();
    }
}
