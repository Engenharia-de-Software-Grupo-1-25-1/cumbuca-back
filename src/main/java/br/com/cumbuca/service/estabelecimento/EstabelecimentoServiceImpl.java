package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.Favorito.FavoritoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoFiltroRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.EstabelecimentoView;
import br.com.cumbuca.model.Favorito;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoViewRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.EstabelecimentoViewRepository;
import br.com.cumbuca.repository.FavoritoRespository;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EstabelecimentoServiceImpl implements EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final EstabelecimentoViewRepository estabelecimentoViewRepository;
    private final AvaliacaoViewRepository avaliacaoViewRepository;
    private final FavoritoRespository favoritoRespository;
    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    public EstabelecimentoServiceImpl(EstabelecimentoRepository estabelecimentoRepository, EstabelecimentoViewRepository estabelecimentoViewRepository, AvaliacaoViewRepository avaliacaoViewRepository, FavoritoRespository favoritoRespository, UsuarioService usuarioService, ModelMapper modelMapper) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.estabelecimentoViewRepository = estabelecimentoViewRepository;
        this.avaliacaoViewRepository = avaliacaoViewRepository;
        this.favoritoRespository = favoritoRespository;
        this.usuarioService = usuarioService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Estabelecimento buscarOuCriar(EstabelecimentoRequestDTO estabelecimentoRequestDTO) {
        return Optional.ofNullable(estabelecimentoRepository.findByNomeAndCategoria(estabelecimentoRequestDTO.getNome(), estabelecimentoRequestDTO.getCategoria()))
                .orElseGet(() -> {
                    final Estabelecimento estabelecimento = modelMapper.map(estabelecimentoRequestDTO, Estabelecimento.class);
                    return estabelecimentoRepository.save(estabelecimento);
                });
    }

    @Override
    public List<EstabelecimentoResponseDTO> listar(EstabelecimentoFiltroRequestDTO filtros, String ordenador) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Example<EstabelecimentoView> example = criarExemplo(filtros);

        final List<EstabelecimentoView> estabelecimentos = (ordenador != null && !ordenador.isBlank())
                ? estabelecimentoViewRepository.findAll(example, Sort.by(Sort.Order.desc(ordenador)))
                : estabelecimentoViewRepository.findAll(example);

        return estabelecimentos.stream()
                .filter(estabelecimento ->
                        filtros.getNotaGeral() == null || (estabelecimento.getNotaGeral() >= filtros.getNotaGeral()
                                && estabelecimento.getNotaGeral() < filtros.getNotaGeral() + 1))
                .map(estabelecimento -> {
                    final List<Avaliacao> avaliacoes = avaliacaoViewRepository.findByEstabelecimentoId(estabelecimento.getId());
                    final EstabelecimentoResponseDTO estabelecimentoResponseDTO = new EstabelecimentoResponseDTO(estabelecimento);
                    estabelecimentoResponseDTO.setQtdAvaliacoes(avaliacoes.size());
                    estabelecimentoResponseDTO.setNotaGeral(estabelecimento.getNotaGeral());
                    estabelecimentoResponseDTO.setFavoritado(favoritoRespository.existsByUsuarioIdAndEstabelecimentoId(usuario.getId(), estabelecimento.getId()));
                    return estabelecimentoResponseDTO;
                })
                .toList();

    }

    private Example<EstabelecimentoView> criarExemplo(EstabelecimentoFiltroRequestDTO filtros) {
        final EstabelecimentoView exemplo = new EstabelecimentoView();

        if (filtros.getNome() != null && !filtros.getNome().isBlank()) {
            exemplo.setNome(filtros.getNome());
        }
        if (filtros.getCategoria() != null && !filtros.getCategoria().isBlank()) {
            exemplo.setCategoria(filtros.getCategoria());
        }
        if (filtros.getLocal() != null && !filtros.getLocal().isBlank()) {
            exemplo.setRua(filtros.getLocal());
            exemplo.setBairro(filtros.getLocal());
            exemplo.setCidade(filtros.getLocal());
            exemplo.setEstado(filtros.getLocal());
        }
        if (filtros.isFavoritado()) {
            exemplo.setFavoritado(true);
        }

        final ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        return Example.of(exemplo, matcher);
    }

    @Override
    public EstabelecimentoResponseDTO recuperar(Long id) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final EstabelecimentoView estabelecimento = estabelecimentoViewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estabelecimento não encontrado."));
        final List<Avaliacao> avaliacoes = avaliacaoViewRepository.findByEstabelecimentoId(estabelecimento.getId());
        final EstabelecimentoResponseDTO estabelecimentoResponseDTO = new EstabelecimentoResponseDTO(estabelecimento);
        estabelecimentoResponseDTO.setQtdAvaliacoes(avaliacoes.size());
        estabelecimentoResponseDTO.setNotaGeral(estabelecimento.getNotaGeral());
        estabelecimentoResponseDTO.setFavoritado(favoritoRespository.existsByUsuarioIdAndEstabelecimentoId(usuario.getId(), estabelecimento.getId()));
        return estabelecimentoResponseDTO;
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
}