package br.com.cumbuca.service.usuario;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.cumbuca.dto.usuario.UsuarioDeleteDTO;
import br.com.cumbuca.dto.usuario.UsuarioUpdateDTO;
import jakarta.transaction.Transactional;
import java.util.Optional;


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
    @Transactional
    public Usuario atualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        if (dto.getEmail() != null) usuario.setEmail(dto.getEmail());
        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getUsername() != null) usuario.setUsername(dto.getUsername());
        if (dto.getDtNascimento() != null) usuario.setDtNascimento(dto.getDtNascimento());

        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        }

        if (dto.getFoto() != null && !dto.getFoto().isEmpty()) {
            try {
                usuario.setFoto(dto.getFoto().getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar a foto.");
            }
        }

        return usuarioRepository.save(usuario);
    }


    @Transactional
    public void excluir(UsuarioDeleteDTO dto) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(dto.getEmail());

        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Credenciais inválidas");
        }

        Usuario usuario = optionalUsuario.get();

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        usuario.setStatus("INATIVO");
        usuario.setNome("Conta Inativa");
        usuario.setFoto(null);

        usuarioRepository.save(usuario);
    }
  //  @Override
  //  @Transactional
   // public void deletar(long id) {
  //      usuarioRepository.deleteById(id);
  //  }




}
