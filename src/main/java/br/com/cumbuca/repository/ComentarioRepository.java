package br.com.cumbuca.repository;

import br.com.cumbuca.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByAvaliacaoId(Long avaliacaoId);

}