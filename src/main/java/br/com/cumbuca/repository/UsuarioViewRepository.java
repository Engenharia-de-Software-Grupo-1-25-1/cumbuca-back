package br.com.cumbuca.repository;

import br.com.cumbuca.model.UsuarioView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioViewRepository extends JpaRepository<UsuarioView, Long> {
    Optional<UsuarioView> findByUsername(String username);

    List<UsuarioView> findByNomeContainingIgnoreCaseOrUsernameContainingIgnoreCase(String nome, String username);
}
