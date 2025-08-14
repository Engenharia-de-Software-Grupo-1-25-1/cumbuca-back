package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.avaliacao.FiltrarAvaliacaoRequestDTO;
import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Foto;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.service.estabelecimento.EstabelecimentoService;
import br.com.cumbuca.service.foto.FotoService;
import br.com.cumbuca.service.tag.TagService;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository, ModelMapper modelMapper,
                                UsuarioService usuarioService, EstabelecimentoService estabelecimentoService, TagService tagService,
                                FotoService fotoService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.modelMapper = modelMapper;
        this.usuarioService = usuarioService;
        this.estabelecimentoService = estabelecimentoService;
        this.tagService = tagService;
        this.fotoService = fotoService;
    }

    @Override
    @Transactional
    public Avaliacao criar(AvaliacaoRequestDTO avaliacaoRequestDTO) {
        final Usuario usuario = usuarioService.getUsuarioLogado();

        modelMapper.typeMap(AvaliacaoRequestDTO.class, Avaliacao.class)
                .addMappings(mapper -> {
                    mapper.skip(Avaliacao::setUsuario);
                    mapper.skip(Avaliacao::setEstabelecimento);
                    mapper.skip(Avaliacao::setFotos);
                    mapper.skip(Avaliacao::setTags);
                });

        final Avaliacao avaliacao = modelMapper.map(avaliacaoRequestDTO, Avaliacao.class);
        final Estabelecimento estabelecimento = estabelecimentoService
                .buscarOuCriar(avaliacaoRequestDTO.getEstabelecimento());
        avaliacao.setUsuario(usuario);
        avaliacao.setEstabelecimento(estabelecimento);

        if (avaliacaoRequestDTO.getFotos() != null && !avaliacaoRequestDTO.getFotos().isEmpty()) {
            final List<Foto> fotos = fotoService.criarFotos(avaliacaoRequestDTO.getFotos(), avaliacao);
            avaliacao.setFotos(fotos);
        }

        if (avaliacaoRequestDTO.getTags() != null && !avaliacaoRequestDTO.getTags().isEmpty()) {
            final List<Tag> tags = tagService.criarTags(avaliacaoRequestDTO.getTags(), avaliacao);
            avaliacao.setTags(tags);
        }

        return avaliacaoRepository.save(avaliacao);
    }

    @Override
    public Avaliacao atualizar(Long id, AvaliacaoRequestDTO dto) {
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada."));

        final Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!avaliacao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new CumbucaException("Você não tem permissão para editar esta avaliação.");
        }

        avaliacao.setItemConsumido(dto.getItemConsumido());
        avaliacao.setDescricao(dto.getDescricao());
        avaliacao.setPreco(dto.getPreco());
        avaliacao.setNotaGeral(dto.getNotaGeral());
        avaliacao.setNotaComida(dto.getNotaComida());
        avaliacao.setNotaAtendimento(dto.getNotaAtendimento());
        avaliacao.setNotaAmbiente(dto.getNotaAmbiente());

        return avaliacaoRepository.save(avaliacao);
    }

    @Override
    public void remover(Long id) {
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada."));

        final Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!avaliacao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new CumbucaException("Você não tem permissão para remover esta avaliação.");
        }

        avaliacaoRepository.delete(avaliacao);
    }

    public List<AvaliacaoResponseDTO> filtrar(FiltrarAvaliacaoRequestDTO filtrarAvaliacaoRequestDTO) {
        Specification<Avaliacao> spec = Specification.where(null);

        if (filtrarAvaliacaoRequestDTO.getFiltrarAvaliacao() != null && filtrarAvaliacaoRequestDTO.getFiltro() != null && !filtrarAvaliacaoRequestDTO.getFiltro().isBlank()) {
            spec = spec.and((root, query, cb) -> {
                switch (filtrarAvaliacaoRequestDTO.getFiltrarAvaliacao()) {
                    case USUARIO:
                        return cb.equal(root.get("usuario").get("nome"), filtrarAvaliacaoRequestDTO.getFiltro());
                    case ESTABELECIMENTO:
                        return cb.equal(root.get("estabelecimento").get("nome"), filtrarAvaliacaoRequestDTO.getFiltro());
                    case ITEM_CONSUMIDO:
                        return cb.equal(root.get("itemConsumido"), filtrarAvaliacaoRequestDTO.getFiltro());
                    case TAGS:
                        return cb.isMember(filtrarAvaliacaoRequestDTO.getFiltro(), root.get("tags"));
                    case PRECO:
                        return cb.equal(root.get("preco"), new BigDecimal(filtrarAvaliacaoRequestDTO.getFiltro()));
                    case NOTA_GERAL:
                        return cb.equal(root.get("notaGeral"), Integer.valueOf(filtrarAvaliacaoRequestDTO.getFiltro()));
                    case NOTA_COMIDA:
                        return cb.equal(root.get("notaComida"), Integer.valueOf(filtrarAvaliacaoRequestDTO.getFiltro()));
                    case NOTA_AMBIENTE:
                        return cb.equal(root.get("notaAmbiente"), Integer.valueOf(filtrarAvaliacaoRequestDTO.getFiltro()));
                    case NOTA_ATENDIMENTO:
                        return cb.equal(root.get("notaAtendimento"), Integer.valueOf(filtrarAvaliacaoRequestDTO.getFiltro()));
                    default:
                        return cb.conjunction();
                }
            });
        }

        Sort sort = Sort.unsorted();
        if (filtrarAvaliacaoRequestDTO.getOrdenacao() != null) {
            switch (filtrarAvaliacaoRequestDTO.getOrdenacao()) {
                case POPULARIDADE:
                    return avaliacaoRepository.findAllOrderByPopularidade()
                        .stream()
                        .map(AvaliacaoResponseDTO::new)
                        .toList();
                case PRECO:
                    sort = Sort.by(Sort.Direction.DESC, "preco");
                    break;
                case MAIS_RECENTE:
                    sort = Sort.by(Sort.Direction.DESC, "data");
                    break;
                case NOTA_GERAL:
                    sort = Sort.by(Sort.Direction.DESC, "notaGeral");
                    break;
            }
        }

        List<Avaliacao> avaliacoes = avaliacaoRepository.findAll(spec, sort);

        return avaliacoes.stream()
                .map(AvaliacaoResponseDTO::new)
                .toList();
    }
}