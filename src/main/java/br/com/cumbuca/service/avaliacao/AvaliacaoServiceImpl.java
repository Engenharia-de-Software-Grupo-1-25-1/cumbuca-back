package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoFiltroRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.exception.CumbucaException;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.AvaliacaoView;
import br.com.cumbuca.model.Comentario;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.AvaliacaoViewRepository;
import br.com.cumbuca.repository.ComentarioRepository;
import br.com.cumbuca.service.estabelecimento.EstabelecimentoService;
import br.com.cumbuca.service.foto.FotoService;
import br.com.cumbuca.service.tag.TagService;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoFacade avaliacaoFacade;
    private final ComentarioRepository comentarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AvaliacaoViewRepository avaliacaoViewRepository;
    private final ModelMapper modelMapper;
    private final UsuarioService usuarioService;
    private final EstabelecimentoService estabelecimentoService;
    private final TagService tagService;
    private final FotoService fotoService;


    public AvaliacaoServiceImpl(AvaliacaoFacade avaliacaoFacade, AvaliacaoRepository avaliacaoRepository, AvaliacaoViewRepository avaliacaoViewRepository,
                                ComentarioRepository comentarioRepository, ModelMapper modelMapper, UsuarioService usuarioService,
                                EstabelecimentoService estabelecimentoService, TagService tagService, FotoService fotoService) {
        this.avaliacaoFacade = avaliacaoFacade;
        this.avaliacaoRepository = avaliacaoRepository;
        this.comentarioRepository = comentarioRepository;
        this.avaliacaoViewRepository = avaliacaoViewRepository;
        this.modelMapper = modelMapper;
        this.usuarioService = usuarioService;
        this.estabelecimentoService = estabelecimentoService;
        this.tagService = tagService;
        this.fotoService = fotoService;
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
    @Transactional
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
    @Transactional(readOnly = true)
    public AvaliacaoResponseDTO recuperar(Long id) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final AvaliacaoView avaliacao = avaliacaoViewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));
        final AvaliacaoResponseDTO avaliacaoResponseDTO = modelMapper.map(avaliacao, AvaliacaoResponseDTO.class);
        
        List<Comentario> comentarios = comentarioRepository.findByAvaliacaoId(avaliacao.getId());
        List<ComentarioResponseDTO> comentariosDTO = comentarios.stream()
                .map(ComentarioResponseDTO::new)
                .toList();

        avaliacaoResponseDTO.setComentarios(comentariosDTO);

        avaliacaoFacade.montarDTORecuperar(avaliacaoResponseDTO, avaliacao.getId(), usuario.getId());
        return avaliacaoResponseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> listar(Long idUsuario, Long idEstabelecimento, AvaliacaoFiltroRequestDTO filtros, String ordenador) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Example<AvaliacaoView> example = criarExemplo(filtros);

        final Sort sort = getSort(ordenador);
        List<AvaliacaoView> avaliacoes = avaliacaoViewRepository.findAll(example, sort);

        if (idUsuario != null) {
            avaliacoes = avaliacaoViewRepository.findByUsuarioIdOrderByDataDesc(idUsuario);
        }
        if (idEstabelecimento != null) {
            avaliacoes = avaliacaoViewRepository.findByEstabelecimentoIdOrderByDataDesc(idEstabelecimento);
        }

        return avaliacoes.stream()
                .filter(avaliacao -> filtrarPorPreco(filtros.getPrecoMinimo(), filtros.getPrecoMaximo(), avaliacao.getPreco()))
                .filter(avaliacao -> filtrarPorTags(filtros.getTags(), avaliacao.getId()))
                .filter(avaliacao ->
                        filtros.getNotaGeral() == null || (avaliacao.getNotaGeral() >= filtros.getNotaGeral()
                                && avaliacao.getNotaGeral() < filtros.getNotaGeral() + 1))
                .filter(avaliacao ->
                        filtros.getNotaComida() == null || (avaliacao.getNotaComida() >= filtros.getNotaComida()
                                && avaliacao.getNotaComida() < filtros.getNotaComida() + 1))
                .filter(avaliacao ->
                        filtros.getNotaAmbiente() == null || (avaliacao.getNotaAmbiente() >= filtros.getNotaAmbiente()
                                && avaliacao.getNotaAmbiente() < filtros.getNotaAmbiente() + 1))
                .filter(avaliacao ->
                        filtros.getNotaAtendimento() == null || (avaliacao.getNotaAtendimento() >= filtros.getNotaAtendimento()
                                && avaliacao.getNotaAtendimento() < filtros.getNotaAtendimento() + 1))
                .map(avaliacao -> {
                    final AvaliacaoResponseDTO avaliacaoResponseDTO = new AvaliacaoResponseDTO(avaliacao);
                    avaliacaoFacade.montarDTOListar(avaliacaoResponseDTO, avaliacao.getId(), usuario.getId());
                    return  avaliacaoResponseDTO;
                })
                .toList();
    }

    private Sort getSort(String ordenador) {
        if ("popularidade".equals(ordenador)) {
            return Sort.by(Sort.Order.desc("qtdCurtidas"), Sort.Order.desc("qtdComentarios"));
        }
        if (ordenador != null && !ordenador.isBlank()) {
            return Sort.by(Sort.Order.desc(ordenador));
        }
        return Sort.by(Sort.Order.desc("data"));
    }

    private Example<AvaliacaoView> criarExemplo(AvaliacaoFiltroRequestDTO filtros) {
        final AvaliacaoView exemplo = modelMapper.map(filtros, AvaliacaoView.class);

        if (filtros.getUsuario() != null && !filtros.getUsuario().isBlank()) {
            final Usuario usuario = new Usuario();
            usuario.setNome(filtros.getUsuario());
            exemplo.setUsuario(usuario);
        }
        if (filtros.getEstabelecimento() != null && !filtros.getEstabelecimento().isBlank()) {
            final Estabelecimento estabelecimento = new Estabelecimento();
            estabelecimento.setNome(filtros.getEstabelecimento());
            exemplo.setEstabelecimento(estabelecimento);
        }

        final ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        return Example.of(exemplo, matcher);
    }

    private boolean filtrarPorPreco(BigDecimal minimo, BigDecimal maximo, BigDecimal preco) {
        if (minimo == null && maximo == null) {
            return true;
        }
        final boolean minimoValido = minimo == null || preco.compareTo(minimo) >= 0;
        final boolean maximoValido = maximo == null || preco.compareTo(maximo) <= 0;
        return minimoValido && maximoValido;
    }

    private boolean filtrarPorTags(List<String> tags, Long avaliacaoId) {
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        final List<String> tagsAvaliacao = tagService.recuperar(avaliacaoId);
        return tagsAvaliacao.stream()
                .anyMatch(tagAvaliacao -> tags.stream()
                        .anyMatch(tagFiltro -> tagFiltro.equalsIgnoreCase(tagAvaliacao)));
    }
}
