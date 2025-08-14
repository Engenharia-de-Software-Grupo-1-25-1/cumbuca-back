package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.repository.AvaliacaoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FiltrarAvaliacaoImpl extends SimpleJpaRepository<Avaliacao, Long> implements AvaliacaoRepository {

    private final EntityManager entityManager;

    public FiltrarAvaliacaoImpl(EntityManager entityManager) {
        super(Avaliacao.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<Avaliacao> findByUsuario_Nome(String filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comUsuarioNomeLike(filtro));
    }

    @Override
    public List<Avaliacao> findByEstabelecimento_Nome(String filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comEstabelecimentoNomeLike(filtro));
    }

    @Override
    public List<Avaliacao> findByItemConsumido(String filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comItemConsumidoLike(filtro));
    }

    @Override
    public List<Avaliacao> findByTags_Tag(String filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comTagNomeLike(filtro));
    }

    @Override
    public List<Avaliacao> findByPreco(BigDecimal filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comPrecoIgual(filtro));
    }

    @Override
    public List<Avaliacao> findByNotaGeral(Integer filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comNotaGeralIgual(filtro));
    }

    @Override
    public List<Avaliacao> findByNotaComida(Integer filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comNotaComidaIgual(filtro));
    }

    @Override
    public List<Avaliacao> findByNotaAmbiente(Integer filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comNotaAmbienteIgual(filtro));
    }

    @Override
    public List<Avaliacao> findByNotaAtendimento(Integer filtro, Sort ordenador) {
        return findAll(AvaliacaoSpecifications.comNotaAtendimentoIgual(filtro));
    }
}