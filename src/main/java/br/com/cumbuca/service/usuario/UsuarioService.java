package br.com.cumbuca.service.usuario;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsuarioService extends UserDetailsService {

    UsuarioResponseDTO criar(UsuarioRequestDTO usuarioRequestDTO);
}
