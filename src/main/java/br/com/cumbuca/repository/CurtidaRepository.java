package br.com.cumbuca.repository;

import br.com.cumbuca.model.Curtida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurtidaRepository extends JpaRepository<Curtida, Long> {

    Integer countByAvaliacao_Id(Long avaliacaoId);

    Curtida findByUsuarioIdAndAvaliacaoId(Long usuarioId, Long avaliacaoId);

}
