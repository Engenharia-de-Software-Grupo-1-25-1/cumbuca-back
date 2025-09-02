package br.com.cumbuca.service.curtida;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Curtida;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.CurtidaRepository;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class CurtidaServiceImpl implements CurtidaService {

    private final UsuarioService usuarioService;
    private final CurtidaRepository curtidaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ModelMapper modelMapper;

    public CurtidaServiceImpl(CurtidaRepository curtidaRepository, UsuarioService usuarioService, AvaliacaoRepository avaliacaoRepository, ModelMapper modelMapper) {
        this.curtidaRepository = curtidaRepository;
        this.usuarioService = usuarioService;
        this.avaliacaoRepository = avaliacaoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CurtidaResponseDTO curtir(Long avaliacaoId) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));

        Curtida curtida = curtidaRepository.findByUsuarioIdAndAvaliacaoId(usuario.getId(), avaliacao.getId());

        if (curtida != null) {
            curtidaRepository.delete(curtida);
            final CurtidaResponseDTO curtidaResponseDTO = modelMapper.map(curtida, CurtidaResponseDTO.class);
            curtidaResponseDTO.setIsCurtida(false);
            return curtidaResponseDTO;
        }

        curtida = new Curtida();
        curtida.setUsuario(usuario);
        curtida.setAvaliacao(avaliacao);
        curtidaRepository.save(curtida);

        final CurtidaResponseDTO curtidaResponseDTO = new CurtidaResponseDTO();
        curtidaResponseDTO.setId(curtida.getId());
        curtidaResponseDTO.setUsuario(modelMapper.map(usuario, UsuarioResponseDTO.class));
        curtidaResponseDTO.setAvaliacao(modelMapper.map(avaliacao, AvaliacaoResponseDTO.class));
        curtidaResponseDTO.setIsCurtida(true);
        return curtidaResponseDTO;
    }

    @Override
    public boolean isAvaliacaoCurtida(Long usuarioId, Long avaliacaoId) {
        return curtidaRepository.existsByUsuarioIdAndAvaliacaoId(usuarioId, avaliacaoId);
    }
}
