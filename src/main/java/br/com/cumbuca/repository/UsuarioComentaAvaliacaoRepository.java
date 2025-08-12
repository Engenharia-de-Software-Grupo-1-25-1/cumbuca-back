package br.com.cumbuca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.cumbuca.model.UsuarioComentaAvaliacao;

@Repository
public interface UsuarioComentaAvaliacaoRepository extends JpaRepository<UsuarioComentaAvaliacao, Long> {

    int countByAvaliacaoId(Long idAvaliacao);

}
