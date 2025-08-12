package br.com.cumbuca.repository;

import br.com.cumbuca.model.UsuarioCurteAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioCurteAvaliacaoRepository extends JpaRepository<UsuarioCurteAvaliacao, Long> {

    int countByAvaliacaoId(Long avaliacaoId);

}
