package br.com.cumbuca.repository;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface AvaliacaoRepository  extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByUsuario_Nome(String filtro);
    List<Avaliacao> findByEstabelecimento_Nome(String filtro);
    List<Avaliacao> findByItemConsumido(String filtro);
    List<Avaliacao> findByTags_Tag(String filtro);
    List<Avaliacao> findByPreco(BigDecimal filtro);
    List<Avaliacao> findByNotaGeral(Integer filtro);
    List<Avaliacao> findByNotaComida(Integer filtro);
    List<Avaliacao> findByNotaAmbiente(Integer filtro);
    List<Avaliacao> findByNotaAtendimento(Integer filtro);
}
