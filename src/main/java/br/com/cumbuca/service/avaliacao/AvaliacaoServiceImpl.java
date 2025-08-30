package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoFiltroRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.exception.CumbucaException;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.AvaliacaoView;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.AvaliacaoViewRepository;
import br.com.cumbuca.service.comentario.ComentarioService;
import br.com.cumbuca.service.estabelecimento.EstabelecimentoService;
import br.com.cumbuca.service.foto.FotoService;
import br.com.cumbuca.service.tag.TagService;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final AvaliacaoViewRepository avaliacaoViewRepository;
    private final ModelMapper modelMapper;
    private final UsuarioService usuarioService;
    private final EstabelecimentoService estabelecimentoService;
    private final TagService tagService;
    private final FotoService fotoService;
    private final ComentarioService comentarioService;
    public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository, AvaliacaoViewRepository avaliacaoViewRepository,
                                ModelMapper modelMapper, UsuarioService usuarioService, EstabelecimentoService estabelecimentoService,
                                TagService tagService, FotoService fotoService, ComentarioService comentarioService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.avaliacaoViewRepository = avaliacaoViewRepository;
        this.modelMapper = modelMapper;
        this.usuarioService = usuarioService;
        this.estabelecimentoService = estabelecimentoService;
        this.tagService = tagService;
        this.fotoService = fotoService;
        this.comentarioService = comentarioService;
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
        final AvaliacaoView avaliacao = avaliacaoViewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));
        final AvaliacaoResponseDTO avaliacaoResponseDTO = modelMapper.map(avaliacao, AvaliacaoResponseDTO.class);
        avaliacaoResponseDTO.setFotos(fotoService.recuperar(id));
        avaliacaoResponseDTO.setTags(tagService.recuperar(id));
        avaliacaoResponseDTO.setComentarios(comentarioService.recuperar(id));
        return avaliacaoResponseDTO;
    }

    @Override
    public List<AvaliacaoResponseDTO> listar(Long idUsuario, Long idEstabelecimento, AvaliacaoFiltroRequestDTO filtros, String ordenador) {
        usuarioService.verificaUsuarioLogado();
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
                .filter(av -> filtrarPorPreco(filtros.getPrecoMinimo(), filtros.getPrecoMaximo(), av.getPreco()))
                .filter(av -> filtrarPorTags(filtros.getTags(), av.getId()))
                .map(av -> {
                    final AvaliacaoResponseDTO dto = new AvaliacaoResponseDTO(av);
                    dto.setFotos(fotoService.recuperar(av.getId()));
                    dto.setTags(tagService.recuperar(av.getId()));
                    return dto;
                })
                .toList();
    }

    private Sort getSort(String ordenador) {
        if (ordenador == null || ordenador.isBlank()) {
            return Sort.by(Sort.Order.desc("data"));
        }
        return switch (ordenador.toLowerCase()) {
            case "popularidade" -> Sort.by(Sort.Order.desc("qtdCurtidas"));
            case "nota" -> Sort.by(Sort.Order.desc("notaGeral"));
            default -> Sort.by(Sort.Order.desc("data"));
        };
    }

    private Example<AvaliacaoView> criarExemplo(AvaliacaoFiltroRequestDTO filtros) {
        final AvaliacaoView exemplo = new AvaliacaoView();

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

        if (filtros.getItemConsumido() != null && !filtros.getItemConsumido().isBlank()) {
            exemplo.setItemConsumido(filtros.getItemConsumido());
        }

        if (filtros.getNotaGeral() != null) {
            exemplo.setNotaGeral(filtros.getNotaGeral());
        }
        if (filtros.getNotaComida() != null) {
            exemplo.setNotaComida(filtros.getNotaComida());
        }
        if (filtros.getNotaAmbiente() != null) {
            exemplo.setNotaAmbiente(filtros.getNotaAmbiente());
        }
        if (filtros.getNotaAtendimento() != null) {
            exemplo.setNotaAtendimento(filtros.getNotaAtendimento());
        }

        final ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        return Example.of(exemplo, matcher);
    }

    private boolean filtrarPorPreco(BigDecimal inicio, BigDecimal fim, BigDecimal preco) {
        if (inicio == null && fim == null) {
            return true;
        }

        final boolean inicioValido = inicio == null || preco.compareTo(inicio) >= 0;
        final boolean fimValido = fim == null || preco.compareTo(fim) <= 0;

        return inicioValido && fimValido;
    }

    private boolean filtrarPorTags(List<String> tagsFiltro, Long avaliacaoId) {
        if (tagsFiltro == null || tagsFiltro.isEmpty()) {
            return true;
        }

        final List<String> tagsAvaliacao = tagService.recuperar(avaliacaoId);

        return tagsAvaliacao.stream()
                .anyMatch(tagAvaliacao -> tagsFiltro.stream()
                        .anyMatch(tagFiltro -> tagFiltro.equalsIgnoreCase(tagAvaliacao)));
    }
}
