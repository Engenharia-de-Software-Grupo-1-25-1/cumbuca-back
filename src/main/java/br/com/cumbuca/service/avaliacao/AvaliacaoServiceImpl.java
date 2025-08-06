package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.repository.AvaliacaoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ModelMapper modelMapper;

    public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository, ModelMapper modelMapper) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Avaliacao criar(AvaliacaoRequestDTO avaliacaoRequestDTO) {
        return null;
    }


}
