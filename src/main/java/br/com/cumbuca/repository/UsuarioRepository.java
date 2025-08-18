package br.com.cumbuca.repository;

import br.com.cumbuca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameOrEmail(String username, String email);
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
}
