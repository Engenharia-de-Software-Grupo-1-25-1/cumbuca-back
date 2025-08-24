package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.Favorito.FavoritoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.model.favorito.Favorito;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.FavoritoRespository;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class EstabelecimentoServiceImpl implements EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final FavoritoRespository favoritoRespository;
    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    public EstabelecimentoServiceImpl(EstabelecimentoRepository estabelecimentoRepository, AvaliacaoRepository avaliacaoRepository, FavoritoRespository favoritoRespository, UsuarioService usuarioService, ModelMapper modelMapper) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.favoritoRespository = favoritoRespository;
        this.usuarioService = usuarioService;
        this.modelMapper = modelMapper;
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

        return estabelecimentos.stream().map(estabelecimento -> {
            final List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estabelecimento.getId());

            final EstabelecimentoResponseDTO estabelecimentoResponseDTO = new EstabelecimentoResponseDTO(estabelecimento);
            estabelecimentoResponseDTO.setQtdAvaliacoes(avaliacoes.size());
            estabelecimentoResponseDTO.setNotaGeral(calculaNotaGeral(avaliacoes));

            return estabelecimentoResponseDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public EstabelecimentoResponseDTO recuperar(Long id) {
        final Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estabelecimento não encontrado."));
        final List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estabelecimento.getId());
        final EstabelecimentoResponseDTO estabelecimentoResponseDTO = new EstabelecimentoResponseDTO(estabelecimento);
        estabelecimentoResponseDTO.setQtdAvaliacoes(avaliacoes.size());
        estabelecimentoResponseDTO.setNotaGeral(calculaNotaGeral(avaliacoes));
        return estabelecimentoResponseDTO;
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
    public FavoritoResponseDTO favoritar(Long id) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estabelecimento não encontrado."));
        Favorito favorito = favoritoRespository.findByUsuarioIdAndEstabelecimentoId(usuario.getId(), estabelecimento.getId());

        if (favorito != null) {
            favoritoRespository.delete(favorito);
            final FavoritoResponseDTO favoritoResponseDTO = new FavoritoResponseDTO(favorito);
            favoritoResponseDTO.setFavoritado(false);
            return favoritoResponseDTO;
        }

        favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setEstabelecimento(estabelecimento);

        final FavoritoResponseDTO favoritoResponseDTO = modelMapper.map(favorito, FavoritoResponseDTO.class);
        favoritoResponseDTO.setFavoritado(true);
        favoritoRespository.save(favorito);
        return favoritoResponseDTO;
    }

    @Override
    public List<EstabelecimentoResponseDTO> pesquisar(String nome) {
        if (nome == null || nome.isBlank()) {
            return estabelecimentoRepository.findAll().stream()
                    .map(EstabelecimentoResponseDTO::new)
                    .toList();
        }

        return estabelecimentoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(EstabelecimentoResponseDTO::new)
                .toList();
    }
}