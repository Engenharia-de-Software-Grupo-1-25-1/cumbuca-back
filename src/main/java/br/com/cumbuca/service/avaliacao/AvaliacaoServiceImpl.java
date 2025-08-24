package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoFiltroRequestDTO;
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

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        avaliacaoResponseDTO.setQtdCurtidas( curtidaRepository.countByAvaliacaoId(avaliacao.getId()));
        avaliacaoResponseDTO.setQtdComentarios(comentarioRepository.countByAvaliacaoId(avaliacao.getId()));
        return avaliacaoResponseDTO;
    }

    @Override
    public List<AvaliacaoResponseDTO> listar(Long idUsuario, Long idEstabelecimento, AvaliacaoFiltroRequestDTO filtros, String ordenacao) {
        usuarioService.verificaUsuarioLogado();
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAllByOrderByDataDesc();
        if (idUsuario != null) {
            avaliacoes = avaliacaoRepository.findByUsuarioIdOrderByDataDesc(idUsuario);
        }
        if (idEstabelecimento != null) {
            avaliacoes = avaliacaoRepository.findByEstabelecimentoIdOrderByDataDesc(idEstabelecimento);
        }

        filtros.validarIntervaloPreco();

        avaliacoes = avaliacoes.stream()
                .filter(avaliacao -> filtrarPorTexto(filtros.getUsuario(), avaliacao.getUsuario().getNome()))
                .filter(avaliacao -> filtrarPorTexto(filtros.getEstabelecimento(), avaliacao.getEstabelecimento().getNome()))
                .filter(avaliacao -> filtrarPorTexto(filtros.getItemConsumido(), avaliacao.getItemConsumido()))
                .filter(avaliacao -> filtrarPorTags(filtros.getTags(), avaliacao.getId()))
                .filter(avaliacao -> filtrarPorPreco(filtros.getPrecoInicio(), filtros.getPrecoFim(), avaliacao.getPreco()))                .filter(avaliacao -> filtrarPorNota(filtros.getNotaGeral(), avaliacao.getNotaGeral()))
                .filter(avaliacao -> filtrarPorNota(filtros.getNotaComida(), avaliacao.getNotaComida()))
                .filter(avaliacao -> filtrarPorNota(filtros.getNotaAmbiente(), avaliacao.getNotaAmbiente()))
                .filter(avaliacao -> filtrarPorNota(filtros.getNotaAtendimento(), avaliacao.getNotaAtendimento()))
                .toList();

        avaliacoes = ordenaAvaliacoes(avaliacoes, ordenacao);

        return avaliacoes.stream()
                .map(avaliacao -> {
                    final AvaliacaoResponseDTO avaliacaoResponseDTO = new AvaliacaoResponseDTO(avaliacao);
                    avaliacaoResponseDTO.setFotos(fotoService.recuperar(avaliacao.getId()));
                    avaliacaoResponseDTO.setTags(tagService.recuperar(avaliacao.getId()));
                    avaliacaoResponseDTO.setQtdCurtidas( curtidaRepository.countByAvaliacaoId(avaliacao.getId()));
                    avaliacaoResponseDTO.setQtdComentarios(comentarioRepository.countByAvaliacaoId(avaliacao.getId()));
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
    public void removerComentario(Long id) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comentário não encontrado"));
        if (!comentario.getUsuario().getId().equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }
        comentarioRepository.delete(comentario);
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

    private List<Avaliacao> ordenaAvaliacoes(List<Avaliacao> avaliacoes, String ordenacao) {
        if (ordenacao == null || avaliacoes == null || avaliacoes.isEmpty()) {
            return avaliacoes;
        }

        final Map<String, Comparator<Avaliacao>> criterios = Map.of(
                "data", Comparator.comparing(Avaliacao::getData, Comparator.nullsLast(Comparator.reverseOrder())),
                "notageral", Comparator.comparing(Avaliacao::getNotaGeral, Comparator.nullsLast(Comparator.reverseOrder()))
        );

        final Comparator<Avaliacao> comparator = criterios.get(ordenacao.toLowerCase());

        if (comparator == null) {
            return avaliacoes;
        }

        return avaliacoes.stream()
                .sorted(comparator)
                .toList();
    }

}
