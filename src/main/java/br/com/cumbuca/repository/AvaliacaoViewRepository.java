package br.com.cumbuca.repository;

import br.com.cumbuca.model.AvaliacaoView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoViewRepository extends JpaRepository<AvaliacaoView, Long> {

    List<AvaliacaoView> findByEstabelecimentoId(Long estabelecimentoId);

    List<AvaliacaoView> findByUsuarioIdOrderByDataDesc(Long usuarioId);

    List<AvaliacaoView> findByEstabelecimentoIdOrderByDataDesc(Long estabelecimentoId);
}