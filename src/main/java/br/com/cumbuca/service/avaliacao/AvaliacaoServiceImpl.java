package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;
import br.com.cumbuca.exception.CumbucaException;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.model.Curtida;
import br.com.cumbuca.model.Comentario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.CurtidaRepository;
import br.com.cumbuca.repository.ComentarioRepository;
import br.com.cumbuca.service.estabelecimento.EstabelecimentoService;
import br.com.cumbuca.service.foto.FotoService;
import br.com.cumbuca.service.tag.TagService;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final ModelMapper modelMapper;
    private final UsuarioService usuarioService;
    private final EstabelecimentoService estabelecimentoService;
    private final TagService tagService;
    private final FotoService fotoService;
    private final ComentarioRepository comentarioRepository;
    private final CurtidaRepository curtidaRepository;

    public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository, ModelMapper modelMapper,
                                UsuarioService usuarioService, EstabelecimentoService estabelecimentoService, TagService tagService,
                                FotoService fotoService, ComentarioRepository comentarioRepository, CurtidaRepository curtidaRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.modelMapper = modelMapper;
        this.usuarioService = usuarioService;
        this.estabelecimentoService = estabelecimentoService;
        this.tagService = tagService;
        this.fotoService = fotoService;
        this.comentarioRepository = comentarioRepository;
        this.curtidaRepository = curtidaRepository;
    }

    @Override
    public AvaliacaoResponseDTO criar(AvaliacaoRequestDTO avaliacaoRequestDTO) {
        final Usuario usuario = usuarioService.getUsuarioLogado();

        final Avaliacao avaliacao = modelMapper.map(avaliacaoRequestDTO, Avaliacao.class);
        final Estabelecimento estabelecimento = estabelecimentoService
                .buscarOuCriar(avaliacaoRequestDTO.getEstabelecimento());
        avaliacao.setUsuario(usuario);
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacaoRepository.save(avaliacao);

        if (avaliacaoRequestDTO.getFotos() != null && !avaliacaoRequestDTO.getFotos().isEmpty()) {
            fotoService.criar(avaliacaoRequestDTO.getFotos(), avaliacao);
        }

        if (avaliacaoRequestDTO.getTags() != null && !avaliacaoRequestDTO.getTags().isEmpty()) {
            tagService.criar(avaliacaoRequestDTO.getTags(), avaliacao);
        }

        return modelMapper.map(avaliacao, AvaliacaoResponseDTO.class);
    }

    @Override
    public AvaliacaoResponseDTO atualizar(Long id, AvaliacaoRequestDTO avaliacaoRequestDTO) {
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada."));

        final Usuario usuario = usuarioService.getUsuarioLogado();
        if (!avaliacao.getUsuario().getId().equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }

        modelMapper.map(avaliacaoRequestDTO, avaliacao);

        fotoService.remover(id);
        tagService.remover(id);

        if (avaliacaoRequestDTO.getFotos() != null) {
            fotoService.criar(avaliacaoRequestDTO.getFotos(), avaliacao);
        }
        
        if (avaliacaoRequestDTO.getTags() != null) {
            tagService.criar(avaliacaoRequestDTO.getTags(), avaliacao);
        }        

        avaliacaoRepository.save(avaliacao);
        return modelMapper.map(avaliacao, AvaliacaoResponseDTO.class);
    }

    @Override
    public void remover(Long id) {
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada."));
        final Usuario usuario = usuarioService.getUsuarioLogado();
        if (!avaliacao.getUsuario().getId().equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }
        avaliacaoRepository.delete(avaliacao);
    }

    @Override
    public AvaliacaoResponseDTO recuperar(Long id) {
        usuarioService.verificaUsuarioLogado();
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));
        final AvaliacaoResponseDTO avaliacaoResponseDTO = modelMapper.map(avaliacao, AvaliacaoResponseDTO.class);
        avaliacaoResponseDTO.setFotos(fotoService.recuperar(id));
        avaliacaoResponseDTO.setTags(tagService.recuperar(id));
        avaliacaoResponseDTO.setQtdCurtidas( curtidaRepository.countByAvaliacao_Id(avaliacao.getId()));
        avaliacaoResponseDTO.setQtdComentarios(comentarioRepository.countByAvaliacao_Id(avaliacao.getId()));
        return avaliacaoResponseDTO;
    }

    @Override
    public List<AvaliacaoResponseDTO> listar(Long idUsuario, Long idEstabelecimento) {
        usuarioService.verificaUsuarioLogado();
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAllByOrderByDataDesc();
        if (idUsuario != null) {
            avaliacoes = avaliacaoRepository.findByUsuarioIdOrderByDataDesc(idUsuario);
        }
        if (idEstabelecimento != null) {
            avaliacoes = avaliacaoRepository.findByEstabelecimentoIdOrderByDataDesc(idEstabelecimento);
        }
        return avaliacoes.stream()
                .map(avaliacao -> {
                    final AvaliacaoResponseDTO avaliacaoResponseDTO = new AvaliacaoResponseDTO(avaliacao);
                    avaliacaoResponseDTO.setFotos(fotoService.recuperar(avaliacao.getId()));
                    avaliacaoResponseDTO.setTags(tagService.recuperar(avaliacao.getId()));
                    avaliacaoResponseDTO.setQtdCurtidas( curtidaRepository.countByAvaliacao_Id(avaliacao.getId()));
                    avaliacaoResponseDTO.setQtdComentarios(comentarioRepository.countByAvaliacao_Id(avaliacao.getId()));
                    return avaliacaoResponseDTO;
                })
                .toList();
    }

    @Override
    public CurtidaResponseDTO curtir(Long id) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));

        Curtida curtida = curtidaRepository.findByUsuarioIdAndAvaliacaoId(usuario.getId(), avaliacao.getId());

        if (curtida != null) {
            if (!curtida.getUsuario().getId().equals(usuario.getId())) {
                throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
            }
            curtidaRepository.delete(curtida);
            final CurtidaResponseDTO curtidaResponseDTO = new CurtidaResponseDTO(curtida);
            curtidaResponseDTO.setCurtido(false);
            return curtidaResponseDTO;
        }

        curtida = new Curtida();
        curtida.setUsuario(usuario);
        curtida.setAvaliacao(avaliacao);

        final CurtidaResponseDTO curtidaResponseDTO = modelMapper.map(curtida, CurtidaResponseDTO.class);
        curtidaResponseDTO.setCurtido(true);
        curtidaRepository.save(curtida);
        return curtidaResponseDTO;
    }

    @Override
    public ComentarioResponseDTO comentar(Long id, String texto) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));

        final Comentario comentario = new Comentario();
        comentario.setAvaliacao(avaliacao);
        comentario.setUsuario(usuario);
        comentario.setComentario(texto);
        comentarioRepository.save(comentario);
        return modelMapper.map(comentario, ComentarioResponseDTO.class);
    }

    @Override
    public void removerComentario(Long id, Long comentarioId) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));

        final Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new NoSuchElementException("Comentário não encontrado"));

        if (!comentario.getUsuario().getId().equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }
        comentarioRepository.delete(comentario);
    }
}
