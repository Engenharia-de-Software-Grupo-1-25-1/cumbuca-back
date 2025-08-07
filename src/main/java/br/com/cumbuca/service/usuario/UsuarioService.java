package br.com.cumbuca.service.usuario;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioUpdateDTO;
import br.com.cumbuca.model.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsuarioService extends UserDetailsService {

    Usuario criar(UsuarioRequestDTO usuarioRequestDTO);

    Usuario atualizar (Long id, UsuarioUpdateDTO updateDTO);

    void deletar (long id);


}
