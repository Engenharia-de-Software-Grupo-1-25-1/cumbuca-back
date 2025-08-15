package br.com.cumbuca.repository;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;

public interface AvaliacaoRepository  extends JpaRepository<Avaliacao, Long> {

    Arrays findByUsuarioIdOrderByCriadoEmDesc(java.lang.Long id);
}
