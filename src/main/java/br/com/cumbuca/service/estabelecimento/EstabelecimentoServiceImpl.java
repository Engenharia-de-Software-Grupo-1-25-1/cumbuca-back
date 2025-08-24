package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.Favorito.FavoritoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoFiltroRequestDTO;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    public List<EstabelecimentoResponseDTO> listar(EstabelecimentoFiltroRequestDTO filtros, boolean ordenar) {
        usuarioService.verificaUsuarioLogado();
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findAll();

        final Map<Long, List<Avaliacao>> avaliacoesMap = verificarAvaliacoes(estabelecimentos);

        estabelecimentos = estabelecimentos.stream()
                .filter(estabelecimento -> filtrarPorTexto(filtros.getNome(), estabelecimento.getNome()))
                .filter(estabelecimento -> filtrarPorTexto(filtros.getCategoria(), estabelecimento.getCategoria()))
                .filter(estabelecimento -> filtrarPorLocal(filtros.getLocal(), estabelecimento))
                .filter(estabelecimento -> filtrarPorHorario(filtros.getHorarioInicio(), filtros.getHorarioFim(), estabelecimento))
                .filter(estabelecimento -> filtrarPorFavorito(filtros.getFavoritos(), estabelecimento))
                .filter(estabelecimento -> {
                    final List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estabelecimento.getId());
                    final double notaGeral = calculaNotaGeral(avaliacoes);
                    return filtrarPorNota(filtros.getNotaGeral(), notaGeral);
                }).toList();

        if (ordenar) {
            estabelecimentos = estabelecimentos.stream()
                    .sorted(Comparator.comparingInt((Estabelecimento est) -> avaliacoesMap.get(est.getId()).size())
                            .reversed())
                    .toList();
        }

        return estabelecimentos.stream().map(estabelecimento -> {
            final List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estabelecimento.getId());
            final EstabelecimentoResponseDTO estabelecimentoResponseDTO = new EstabelecimentoResponseDTO(estabelecimento);
            estabelecimentoResponseDTO.setQtdAvaliacoes(avaliacoes.size());
            estabelecimentoResponseDTO.setNotaGeral(calculaNotaGeral(avaliacoes));
            return estabelecimentoResponseDTO;
        }).toList();
    }

    private Map<Long, List<Avaliacao>> verificarAvaliacoes(List<Estabelecimento> estabelecimentos) {
        final Map<Long, List<Avaliacao>> avaliacoesMap = estabelecimentos.stream()
                .collect(Collectors.toMap(
                        Estabelecimento::getId,
                        estabelecimento -> avaliacaoRepository.findByEstabelecimentoId(estabelecimento.getId())
                ));

        estabelecimentos = estabelecimentos.stream()
                .filter(est -> !avaliacoesMap.get(est.getId()).isEmpty())
                .toList();

        if (estabelecimentos.isEmpty()) {
            throw new NoSuchElementException("Estabelecimento não encontrado, pois não há avaliações");
        }

        return avaliacoesMap;
    }

    private boolean filtrarPorTexto(String filtro, String valor) {
        return filtro == null || filtro.isBlank() ||
                (valor != null && valor.toLowerCase().contains(filtro.toLowerCase()));
    }

    private boolean filtrarPorNota(Double notaFiltro, Double nota) {
        return notaFiltro == null || nota.equals(notaFiltro);
    }

    private boolean filtrarPorLocal(String filtro, Estabelecimento estabelecimento) {
        if (filtro == null || filtro.isBlank()) {
            return true;
        }
        return (estabelecimento.getRua() != null && estabelecimento.getRua().toLowerCase().contains(filtro.toLowerCase()) ||
                estabelecimento.getBairro() != null && estabelecimento.getBairro().toLowerCase().contains(filtro.toLowerCase())) ||
                (estabelecimento.getCidade() != null && estabelecimento.getCidade().toLowerCase().contains(filtro.toLowerCase())) ||
                (estabelecimento.getEstado() != null && estabelecimento.getEstado().toLowerCase().contains(filtro.toLowerCase()));
    }

    private boolean filtrarPorHorario(String horarioInicioFiltro, String horarioFimFiltro, Estabelecimento estabelecimento) {
        if ((horarioInicioFiltro == null || horarioInicioFiltro.isBlank()) &&
                (horarioFimFiltro == null || horarioFimFiltro.isBlank())) {
            return true;
        }

        return estabelecimento.getHorarios().stream().anyMatch(horario -> {
            if (horario.getHorario() == null || horario.getHorario().isBlank()) {
                return false;
            }

            final String[] partes = horario.getHorario().split("-");
            if (partes.length != 2) {
                return false;
            }

            final String inicio = partes[0].trim();
            final String fim = partes[1].trim();

            final boolean inicioValido = horarioInicioFiltro == null || inicio.compareTo(horarioInicioFiltro) >= 0;
            final boolean fimValido = horarioFimFiltro == null || fim.compareTo(horarioFimFiltro) <= 0;

            return inicioValido && fimValido;
        });
    }

    private boolean filtrarPorFavorito(String favoritoFiltro, Estabelecimento estabelecimento) {
        if (favoritoFiltro == null || favoritoFiltro.isBlank()) {
            return true;
        }

        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Favorito favorito = favoritoRespository.findByUsuarioIdAndEstabelecimentoId(usuario.getId(), estabelecimento.getId());

        return favorito != null;
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
}