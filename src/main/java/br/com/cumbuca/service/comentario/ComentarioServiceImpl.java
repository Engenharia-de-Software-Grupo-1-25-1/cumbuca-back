package br.com.cumbuca.service.comentario;

import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Comentario;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.ComentarioRepository;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioService usuarioService;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ModelMapper modelMapper;

    public ComentarioServiceImpl(ComentarioRepository comentarioRepository,  UsuarioService usuarioService, AvaliacaoRepository avaliacaoRepository, ModelMapper modelMapper) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioService = usuarioService;
        this.avaliacaoRepository = avaliacaoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ComentarioResponseDTO> recuperar(Long avaliacaoId) {
        final List<Comentario> comentarios = comentarioRepository.findByAvaliacaoId(avaliacaoId);
        return comentarios.stream()
                .map(ComentarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public ComentarioResponseDTO comentar(Long avaliacaoId, String texto) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));
        final Comentario comentario = new Comentario();
        comentario.setAvaliacao(avaliacao);
        comentario.setUsuario(usuario);
        comentario.setComentario(texto);
        comentarioRepository.save(comentario);
        return modelMapper.map(comentario, ComentarioResponseDTO.class);
    }

    @Override
    public void remover(Long id) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comentário não encontrado"));
        if (!comentario.getUsuario().getId().equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }
        comentarioRepository.delete(comentario);
    }
}
