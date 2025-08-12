package br.com.cumbuca.service.feed;

import br.com.cumbuca.dto.feed.AvaliacaoResumidaDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.UsuarioComentaAvaliacaoRepository;
import br.com.cumbuca.repository.UsuarioCurteAvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl implements FeedService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioCurteAvaliacaoRepository usuarioCurteAvaliacaoRepository;
    private final UsuarioComentaAvaliacaoRepository usuarioComentaAvaliacaoRepository;

    public FeedServiceImpl(AvaliacaoRepository avaliacaoRepository,
            UsuarioCurteAvaliacaoRepository usuarioCurteAvaliacaoRepository,
            UsuarioComentaAvaliacaoRepository usuarioComentaAvaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioCurteAvaliacaoRepository = usuarioCurteAvaliacaoRepository;
        this.usuarioComentaAvaliacaoRepository = usuarioComentaAvaliacaoRepository;
    }

    @Override
    public List<AvaliacaoResumidaDTO> getAvaliacoes() {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAllWithUserEstablishmentPhotosTags();

        return avaliacoes.stream().map(avaliacao -> {
            int qtdCurtidas = usuarioCurteAvaliacaoRepository.countByAvaliacaoId(avaliacao.getId());
            int qtdComentarios = usuarioComentaAvaliacaoRepository.countByAvaliacaoId(avaliacao.getId());

            final Usuario usuario = avaliacao.getUsuario();

            final var fotosBase64 = avaliacao.getFotos().stream()
                    .map(foto -> Base64.getEncoder().encodeToString(foto.getFoto()))
                    .collect(Collectors.toList());

            final var tags = avaliacao.getTags().stream()
                    .map(Tag::getTag)
                    .collect(Collectors.toList());

            return new AvaliacaoResumidaDTO(
                    usuario.getNome(),
                    usuario.getFoto() != null ? Base64.getEncoder().encodeToString(usuario.getFoto()) : null,
                    avaliacao.getEstabelecimento().getNome(),
                    avaliacao.getNotaGeral(),
                    avaliacao.getDescricao(),
                    qtdCurtidas,
                    qtdComentarios,
                    fotosBase64,
                    tags,
                    avaliacao.getData());
        }).collect(Collectors.toList());
    }
}