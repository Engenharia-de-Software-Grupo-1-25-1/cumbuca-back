package br.com.cumbuca.repository;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface AvaliacaoRepository  extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByUsuario_Nome(String filtro, Sort ordenador);
    List<Avaliacao> findByEstabelecimento_Nome(String filtro, Sort ordenador);
    List<Avaliacao> findByItemConsumido(String filtro, Sort ordenador);
    List<Avaliacao> findByTags_Tag(String filtro, Sort ordenador);
    List<Avaliacao> findByPreco(BigDecimal filtro, Sort ordenador);
    List<Avaliacao> findByNotaGeral(Integer filtro, Sort ordenador);
    List<Avaliacao> findByNotaComida(Integer filtro, Sort ordenador);
    List<Avaliacao> findByNotaAmbiente(Integer filtro, Sort ordenador);
    List<Avaliacao> findByNotaAtendimento(Integer filtro, Sort ordenador);
}
