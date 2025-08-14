package br.com.cumbuca.repository;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvaliacaoRepository  extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findAll(Specification<Avaliacao> spec, Sort sort);

    @Query(value = """
        SELECT a.*
        FROM avaliacao a
        LEFT JOIN usuario_curte_avaliacao uc ON uc.id_avaliacao = a.id
        LEFT JOIN usuario_comenta_avaliacao cm ON cm.id_avaliacao = a.id
        GROUP BY a.id
        ORDER BY COUNT(DISTINCT uc.id_usuario) + COUNT(DISTINCT cm.id_usuario) DESC
    """, nativeQuery = true)
    List<Avaliacao> findAllOrderByPopularidade();
}
