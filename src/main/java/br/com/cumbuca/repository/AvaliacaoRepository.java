package br.com.cumbuca.repository;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByEstabelecimentoId(Long estabelecimentoId);

    List<Avaliacao> findAllByOrderByDataDesc();

    List<Avaliacao> findByUsuarioIdOrderByDataDesc(Long usuarioId);

    List<Avaliacao> findByEstabelecimentoIdOrderByDataDesc(Long idEstabelecimentoId);

}
