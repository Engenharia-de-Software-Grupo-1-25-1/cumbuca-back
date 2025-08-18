package br.com.cumbuca.service.usuario;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.exception.CumbucaException;
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

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    @Override
    public UsuarioResponseDTO criar(UsuarioRequestDTO usuarioRequestDTO) {
        modelMapper.typeMap(UsuarioRequestDTO.class, Usuario.class)
                .addMappings(mapper -> mapper.skip(Usuario::setSenha));
        final Usuario usuario = modelMapper.map(usuarioRequestDTO, Usuario.class);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        if (usuarioRequestDTO.getFoto() != null && !usuarioRequestDTO.getFoto().isEmpty()) {
            try {
                usuario.setFoto(usuarioRequestDTO.getFoto().getBytes());
            } catch (IOException e) {
                throw new CumbucaException("Erro ao processar a foto.");
            }
        }

        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    @Override
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        final Usuario usuario = getUsuarioLogado();
        if (!id.equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }

        modelMapper.typeMap(UsuarioRequestDTO.class, Usuario.class)
                .addMappings(mapper -> mapper.skip(Usuario::setSenha));
        modelMapper.map(usuarioRequestDTO, usuario);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        if (usuarioRequestDTO.getFoto() != null && !usuarioRequestDTO.getFoto().isEmpty()) {
            try {
                usuario.setFoto(usuarioRequestDTO.getFoto().getBytes());
            } catch (IOException e) {
                throw new CumbucaException("Erro ao processar a foto.");
            }
        }
        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    @Override
    public void remover(Long id) {
        final Usuario usuario = getUsuarioLogado();
        if (!id.equals(usuario.getId())) {
            throw new CumbucaException("Usuário não tem permissão para realizar esta ação.");
        }
        usuarioRepository.delete(usuario);
    }

    @Override
    public UsuarioResponseDTO recuperar(Long id) {
        verificaUsuarioLogado();
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    @Override
    public UsuarioResponseDTO recuperarPorUsername(String username) {
        verificaUsuarioLogado();
        final Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    @Override
    public Usuario getUsuarioLogado() {
        verificaUsuarioLogado();
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String login = authentication.getName();
        return usuarioRepository.findByUsernameOrEmail(login, login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    public void verificaUsuarioLogado() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("Usuário não autenticado");
        }
    }

}
