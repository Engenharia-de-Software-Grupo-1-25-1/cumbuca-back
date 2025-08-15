package br.com.cumbuca.repository;

import br.com.cumbuca.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<String> findTagsPopulares();
}
