package br.com.cumbuca.repository;

import br.com.cumbuca.model.UsuarioView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioViewRepository extends JpaRepository<UsuarioView, Long> {
    List<UsuarioView> findByNomeContainingIgnoreCaseOrUsernameContainingIgnoreCase(String nome, String username);
}
