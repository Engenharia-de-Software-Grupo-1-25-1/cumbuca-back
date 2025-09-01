package br.com.cumbuca.service.usuario;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.model.UsuarioView;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.repository.UsuarioViewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioViewRepository usuarioViewRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioViewRepository usuarioViewRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioViewRepository = usuarioViewRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    @Override
    public UsuarioResponseDTO criar(UsuarioRequestDTO usuarioRequestDTO) throws IOException {
        modelMapper.typeMap(UsuarioRequestDTO.class, Usuario.class)
                .addMappings(mapper -> mapper.skip(Usuario::setSenha));
        final Usuario usuario = modelMapper.map(usuarioRequestDTO, Usuario.class);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        if (usuarioRequestDTO.getFoto() != null && !usuarioRequestDTO.getFoto().isEmpty()) {
            usuario.setFoto(usuarioRequestDTO.getFoto().getBytes());
        }

        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO usuarioRequestDTO) throws IOException {
        final Usuario usuario = getUsuarioLogado();
        if (!id.equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }

        modelMapper.typeMap(UsuarioRequestDTO.class, Usuario.class)
                .addMappings(mapper -> mapper.skip(Usuario::setSenha));
        modelMapper.map(usuarioRequestDTO, usuario);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        if (usuarioRequestDTO.getFoto() != null && !usuarioRequestDTO.getFoto().isEmpty()) {
            usuario.setFoto(usuarioRequestDTO.getFoto().getBytes());
        }

        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public void remover(Long id) {
        final Usuario usuario = getUsuarioLogado();
        if (!id.equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }
        usuario.setStatus("INATIVO");
        usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioResponseDTO recuperar(Long id) {
        final UsuarioView usuario = usuarioViewRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new UsuarioResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO recuperar(String username) {
        final UsuarioView usuario = usuarioViewRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new UsuarioResponseDTO(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> listar(String nome) {
        if (nome == null || nome.isBlank()) {
            return usuarioViewRepository.findAll().stream()
                    .map(UsuarioResponseDTO::new)
                    .toList();
        }
        return usuarioViewRepository.findByNomeContainingIgnoreCaseOrUsernameContainingIgnoreCase(nome, nome).stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    @Override
    public Usuario getUsuarioLogado() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String login = authentication.getName();
        return usuarioRepository.findByUsernameOrEmail(login, login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }
}
