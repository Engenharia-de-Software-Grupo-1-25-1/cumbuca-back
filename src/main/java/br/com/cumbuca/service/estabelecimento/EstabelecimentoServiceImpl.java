package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoDetalheResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResumoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.model.UsuarioFavoritaEstabelecimentoId;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.FavoritoRepository;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class EstabelecimentoServiceImpl implements EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final FavoritoRepository favoritoRepository;
    private final ModelMapper modelMapper;
    private final UsuarioService usuarioService;

    public EstabelecimentoServiceImpl(EstabelecimentoRepository estabelecimentoRepository, FavoritoRepository favoritoRepository, ModelMapper modelMapper, UsuarioService usuarioService) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.favoritoRepository = favoritoRepository;
        this.modelMapper = modelMapper;
        this.usuarioService = usuarioService;
    }

    @Override
    public Estabelecimento buscarOuCriar(EstabelecimentoRequestDTO estabelecimentoRequestDTO) {
        return estabelecimentoRepository.findById(estabelecimentoRequestDTO.getId())
                .orElseGet(() -> {
                    final Estabelecimento novo = modelMapper.map(estabelecimentoRequestDTO, Estabelecimento.class);
                    return estabelecimentoRepository.save(novo);
                });
    }

    @Override
    public List<EstabelecimentoResumoResponseDTO> listarEstabelecimentosResumidos() {
        return estabelecimentoRepository.findAll().stream()
                .map(this::toResumoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EstabelecimentoDetalheResponseDTO buscarDetalhesEstabelecimento(Long id) {
        final Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estabelecimento não encontrado."));

        final List<Avaliacao> avaliacoes = estabelecimento.getAvaliacoes();
        final double notaGeralMedia = avaliacoes.stream()
                .mapToDouble(Avaliacao::getNotaGeral)
                .average()
                .orElse(0.0);

        final long quantidadeAvaliacoes = avaliacoes.size();

        final BigDecimal precoMedio = avaliacoes.stream()
                .map(Avaliacao::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal precoMedioFinal = quantidadeAvaliacoes > 0 ? precoMedio.divide(BigDecimal.valueOf(quantidadeAvaliacoes), RoundingMode.HALF_UP) : BigDecimal.ZERO;

        final List<AvaliacaoResponseDTO> avaliacaoResponseDTOS = avaliacoes.stream()
                .map(AvaliacaoResponseDTO::new)
                .toList();

        final List<String> tagsPopulares = avaliacoes.stream()
                .flatMap(avaliacao -> avaliacao.getTags().stream())
                .map(tag -> tag.getTag())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        final Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        final boolean isFavorito = favoritoRepository.existsById(new UsuarioFavoritaEstabelecimentoId(usuarioLogado.getId(), id));

        return new EstabelecimentoDetalheResponseDTO(
                estabelecimento,
                notaGeralMedia,
                quantidadeAvaliacoes,
                precoMedioFinal,
                avaliacaoResponseDTOS,
                tagsPopulares,
                isFavorito
        );
    }


    private EstabelecimentoResumoResponseDTO toResumoDTO(Estabelecimento estabelecimento) {
        final List<Avaliacao> avaliacoes = estabelecimento.getAvaliacoes();
        final double notaGeral = avaliacoes.stream()
                .mapToDouble(Avaliacao::getNotaGeral)
                .average()
                .orElse(0.0);

        final long quantidadeAvaliacoes = avaliacoes.size();

        final BigDecimal precoMedio = avaliacoes.stream()
                .map(Avaliacao::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal precoMedioFinal = quantidadeAvaliacoes > 0 ? precoMedio.divide(BigDecimal.valueOf(quantidadeAvaliacoes), RoundingMode.HALF_UP) : BigDecimal.ZERO;

        return new EstabelecimentoResumoResponseDTO(
                estabelecimento.getId(),
                estabelecimento.getNome(),
                estabelecimento.getCategoria(),
                estabelecimento.getRua(),
                estabelecimento.getNumero(),
                estabelecimento.getBairro(),
                estabelecimento.getCidade(),
                notaGeral,
                quantidadeAvaliacoes,
                precoMedioFinal
        );
    }
}