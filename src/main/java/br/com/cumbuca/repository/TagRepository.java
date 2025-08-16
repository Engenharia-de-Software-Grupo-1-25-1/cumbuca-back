package br.com.cumbuca.repository;

import br.com.cumbuca.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    @Query("""
        SELECT t.tag AS nome, COUNT(t) AS quantidade
        FROM Tag t
        GROUP BY t.tag
        ORDER BY COUNT(t) DESC
        LIMIT 5
    """)
    List<Object[]> findTop5TagsPopulares();
    
    List<Tag> findByAvaliacaoId(Long avaliacaoId);
}
