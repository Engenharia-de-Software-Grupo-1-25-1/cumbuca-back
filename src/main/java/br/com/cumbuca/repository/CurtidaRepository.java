package br.com.cumbuca.repository;

import br.com.cumbuca.model.Curtida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurtidaRepository extends JpaRepository<Curtida, Long> {

    Curtida findByUsuarioIdAndAvaliacaoId(Long usuarioId, Long avaliacaoId);

    boolean existsByUsuarioIdAndAvaliacaoId(Long usuarioId, Long avaliacaoId);

}