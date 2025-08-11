package br.com.cumbuca.service.usuario;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado."));
    }

    @Override
    public Usuario criar(UsuarioRequestDTO usuarioRequestDTO) {
        modelMapper.typeMap(UsuarioRequestDTO.class, Usuario.class)
                .addMappings(mapper -> mapper.skip(Usuario::setSenha));

        Usuario usuario = modelMapper.map(usuarioRequestDTO, Usuario.class);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        if (usuarioRequestDTO.getFoto() != null && !usuarioRequestDTO.getFoto().isEmpty()) {
            try {
                usuario.setFoto(usuarioRequestDTO.getFoto().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        usuario = usuarioRepository.save(usuario);
        return usuario;
    }

    @Override
    public Usuario atualizar(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuarioRequestDTO.getFoto() != null && !usuarioRequestDTO.getFoto().isEmpty()) {
            try {
                usuario.setFoto(usuarioRequestDTO.getFoto().getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar a foto.");
            }
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public void remover(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new CumbucaException("Usuário não encontrado"));
        usuarioRepository.delete(usuario);
    }

    @Override
    public Usuario getUsuarioLogado() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String login = authentication.getName();

        if (!authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("Usuário não autenticado");
        }

        return usuarioRepository.findByUsernameOrEmail(login, login)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado."));
    }
}
