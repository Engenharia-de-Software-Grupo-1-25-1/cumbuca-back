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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
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
    public List<AvaliacaoResponseDTO> listar(Long idUsuario, Long idEstabelecimento, AvaliacaoFiltroRequestDTO filtros, String ordenacao) {
        usuarioService.verificaUsuarioLogado();
        List<AvaliacaoView> avaliacoes = avaliacaoViewRepository.findAllByOrderByDataDesc();
        if (idUsuario != null) {
            avaliacoes = avaliacaoViewRepository.findByUsuarioIdOrderByDataDesc(idUsuario);
        }
        if (idEstabelecimento != null) {
            avaliacoes = avaliacaoViewRepository.findByEstabelecimentoIdOrderByDataDesc(idEstabelecimento);
        }

        avaliacoes = avaliacoes.stream()
                .filter(avaliacao -> filtrarPorTexto(filtros.getUsuario(), avaliacao.getUsuario().getNome()))
                .filter(avaliacao -> filtrarPorTexto(filtros.getEstabelecimento(), avaliacao.getEstabelecimento().getNome()))
                .filter(avaliacao -> filtrarPorTexto(filtros.getItemConsumido(), avaliacao.getItemConsumido()))
                .filter(avaliacao -> filtrarPorTags(filtros.getTags(), avaliacao.getId()))
                .filter(avaliacao -> filtrarPorPreco(filtros.getPrecoInicio(), filtros.getPrecoFim(), avaliacao.getPreco()))
                .filter(avaliacao -> filtrarPorNota(filtros.getNotaGeral(), avaliacao.getNotaGeral()))
                .filter(avaliacao -> filtrarPorNota(filtros.getNotaComida(), avaliacao.getNotaComida()))
                .filter(avaliacao -> filtrarPorNota(filtros.getNotaAmbiente(), avaliacao.getNotaAmbiente()))
                .filter(avaliacao -> filtrarPorNota(filtros.getNotaAtendimento(), avaliacao.getNotaAtendimento()))
                .toList();

        if (ordenacao != null && !ordenacao.isBlank()) {
            avaliacoes = criteriosOrdenacao(avaliacoes, ordenacao);
        }

        return avaliacoes.stream()
                .map(avaliacao -> {
                    final AvaliacaoResponseDTO avaliacaoResponseDTO = new AvaliacaoResponseDTO(avaliacao);
                    avaliacaoResponseDTO.setFotos(fotoService.recuperar(avaliacao.getId()));
                    avaliacaoResponseDTO.setTags(tagService.recuperar(avaliacao.getId()));
                    return avaliacaoResponseDTO;
                })
                .toList();
    }

    private List<AvaliacaoView> criteriosOrdenacao(List<AvaliacaoView> avaliacoes, String ordenacao) {
        if ("popularidade".equalsIgnoreCase(ordenacao)) {
            return avaliacoes.stream()
                    .sorted(Comparator.comparingInt((AvaliacaoView av) ->
                            (av.getQtdCurtidas() == null ? 0 : av.getQtdCurtidas()) +
                                    (av.getQtdComentarios() == null ? 0 : av.getQtdComentarios())
                    ).reversed())
                    .toList();
        } else if ("notageral".equalsIgnoreCase(ordenacao)) {
            return avaliacoes.stream()
                    .sorted(Comparator.comparing(AvaliacaoView::getNotaGeral).reversed())
                    .toList();
        } else {
            return avaliacoes.stream()
                    .sorted(Comparator.comparing(AvaliacaoView::getData).reversed())
                    .toList();
        }
    }

    private boolean filtrarPorTexto(String filtro, String valor) {
        return filtro == null || filtro.isBlank() ||
                (valor != null && valor.toLowerCase().contains(filtro.toLowerCase()));
    }

    private boolean filtrarPorPreco(BigDecimal inicio, BigDecimal fim, BigDecimal preco) {
        if (inicio == null && fim == null) {
            return true;
        }

        final boolean inicioValido = inicio == null || preco.compareTo(inicio) >= 0;
        final boolean fimValido = fim == null || preco.compareTo(fim) <= 0;

        return inicioValido && fimValido;
    }

    private boolean filtrarPorNota(Integer notaFiltro, Integer nota) {
        return notaFiltro == null || nota.equals(notaFiltro);
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
