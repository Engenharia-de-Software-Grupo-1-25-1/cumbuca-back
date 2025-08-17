package br.com.cumbuca.repository;

import br.com.cumbuca.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {
    List<Foto> findByAvaliacaoId(Long avaliacaoId);
}
