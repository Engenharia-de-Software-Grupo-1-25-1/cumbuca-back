package br.com.cumbuca.repository;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvaliacaoRepository  extends JpaRepository<Avaliacao, Long> {

    @EntityGraph(attributePaths = {"usuario", "estabelecimento", "fotos", "tags"})
    List<Avaliacao> findAllByOrderByDataDesc();

}

