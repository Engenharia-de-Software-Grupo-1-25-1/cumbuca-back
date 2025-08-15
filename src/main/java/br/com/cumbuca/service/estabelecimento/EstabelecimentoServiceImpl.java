package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResumoResponseDTO;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return estabelecimentoRepository.findEstabelecimentosSummary();
    }
}
