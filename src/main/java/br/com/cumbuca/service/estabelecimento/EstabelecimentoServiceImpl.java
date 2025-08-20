package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class EstabelecimentoServiceImpl implements EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;
    //private final FavoritoRepository favoritoRepository;

    public EstabelecimentoServiceImpl(EstabelecimentoRepository estabelecimentoRepository, AvaliacaoRepository avaliacaoRepository, UsuarioService usuarioService, ModelMapper modelMapper) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioService = usuarioService;
        this.modelMapper = modelMapper;
        //this.favoritoRepository = favoritoRepository;
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
    public List<EstabelecimentoResponseDTO> listar() {
        usuarioService.verificaUsuarioLogado();
        final List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findAll();

        return estabelecimentos.stream().map(estab -> {
            final List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estab.getId());

            final EstabelecimentoResponseDTO estabelecimentoResponseDTO = new EstabelecimentoResponseDTO(estab);
            estabelecimentoResponseDTO.setQtdAvaliacoes(avaliacoes.size());
            estabelecimentoResponseDTO.setNotaGeral(calculaNotaGeral(avaliacoes));

            return estabelecimentoResponseDTO;
        }).collect(Collectors.toList());
    }

    private double calculaNotaGeral(List<Avaliacao> avaliacoes) {
        if (avaliacoes == null || avaliacoes.isEmpty()) {
            return 0.0;
        }
        return avaliacoes.stream()
                .mapToDouble(Avaliacao::getNotaGeral)
                .average()
                .orElse(0.0);
    }

    @Override
    public EstabelecimentoResponseDTO buscarDetalhesEstabelecimento(Long id) {
        final Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estabelecimento não encontrado."));
        final List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estabelecimento.getId());
        final double notaGeralMedia = avaliacoes.stream()
                .mapToDouble(Avaliacao::getNotaGeral)
                .average()
                .orElse(0.0);
        final long quantidadeAvaliacoes = avaliacoes.size();
        final List<AvaliacaoResponseDTO> avaliacaoResponseDTOS = avaliacoes.stream()
                .map(AvaliacaoResponseDTO::new)
                .toList();
        //final Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        // final boolean isFavorito = favoritoRepository.existsById(new UsuarioFavoritaEstabelecimentoId(usuarioLogado.getId(), id));
        return new EstabelecimentoResponseDTO(
                estabelecimento,
                notaGeralMedia,
                quantidadeAvaliacoes,
                avaliacaoResponseDTOS
                );
    }
}