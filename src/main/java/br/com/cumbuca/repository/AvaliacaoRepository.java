package br.com.cumbuca.repository;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvaliacaoRepository  extends JpaRepository<Avaliacao, Long> {


    @Query("SELECT DISTINCT a FROM Avaliacao a " +
            "JOIN FETCH a.usuario u " +
            "JOIN FETCH a.estabelecimento e " +
            "LEFT JOIN FETCH a.fotos f " +
            "LEFT JOIN FETCH a.tags t " +
            "ORDER BY a.data DESC")
    List<Avaliacao> findAllWithUserEstablishmentPhotosTags();

}
