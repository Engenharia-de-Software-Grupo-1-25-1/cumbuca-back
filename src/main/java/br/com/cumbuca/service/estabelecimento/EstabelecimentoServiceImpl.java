package br.com.cumbuca.service.estabelecimento;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class EstabelecimentoServiceImpl implements EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ModelMapper modelMapper;

    public EstabelecimentoServiceImpl(EstabelecimentoRepository estabelecimentoRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public Estabelecimento buscarOuCriar(EstabelecimentoRequestDTO estabelecimentoRequestDTO) {
        return estabelecimentoRepository.findByNomeAndRuaAndCidadeAndEstadoAndCep(
                        estabelecimentoRequestDTO.getNome(), estabelecimentoRequestDTO.getRua()
                        , estabelecimentoRequestDTO.getCidade()
                        , estabelecimentoRequestDTO.getEstado()
                        , estabelecimentoRequestDTO.getCep())
                .orElseGet(() -> {
                    final Estabelecimento novo = modelMapper.map(estabelecimentoRequestDTO, Estabelecimento.class);
                    return estabelecimentoRepository.save(novo);
                });
    }
}
