package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResumoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstabelecimentoServiceImpl implements EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ModelMapper modelMapper;

    public EstabelecimentoServiceImpl(EstabelecimentoRepository estabelecimentoRepository, ModelMapper modelMapper) {
        this.estabelecimentoRepository = estabelecimentoRepository;
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
    public List<EstabelecimentoResumoResponseDTO> listarEstabelecimentosResumidos() {
        return estabelecimentoRepository.findAll().stream()
                .map(this::toResumoDTO)
                .collect(Collectors.toList());
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