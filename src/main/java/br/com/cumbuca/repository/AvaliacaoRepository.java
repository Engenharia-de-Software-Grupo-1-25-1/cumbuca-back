package br.com.cumbuca.repository;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    @Query("" +
            "SELECT a FROM Avaliacao " +
            "a JOIN FETCH a.usuario " +
            "JOIN FETCH a.estabelecimento " +
            "ORDER BY a.data DESC")
    List<Avaliacao> findAllParaFeed();
}
