package br.com.cumbuca.service.usuario;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsuarioService extends UserDetailsService {

    UsuarioResponseDTO criar(UsuarioRequestDTO usuarioRequestDTO);

    UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO usuarioRequestDTO);

    void remover(Long id);

    UsuarioResponseDTO recuperar(Long id);

    UsuarioResponseDTO recuperar(String username);

    List<UsuarioResponseDTO> listar(String nome);

    Usuario getUsuarioLogado();

    void verificaUsuarioLogado();
}
