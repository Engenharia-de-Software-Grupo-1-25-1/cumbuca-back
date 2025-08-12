package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.math.BigDecimal;

public class AvaliacaoSpecifications {

    public static Specification<Avaliacao> comUsuarioNomeLike(String nome) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("usuario").get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Avaliacao> comEstabelecimentoNomeLike(String nome) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("estabelecimento").get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Avaliacao> comItemConsumidoLike(String item) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("itemConsumido")), "%" + item.toLowerCase() + "%");
    }

    public static Specification<Avaliacao> comTagNomeLike(String tagNome) {
        return (root, query, cb) -> {
            Join<Avaliacao, Tag> tagJoin = root.join("tags");
            return cb.like(cb.lower(tagJoin.get("tag")), "%" + tagNome.toLowerCase() + "%");
        };
    }

    public static Specification<Avaliacao> comPrecoIgual(BigDecimal preco) {
        return (root, query, cb) -> cb.equal(root.get("preco"), preco);
    }

    public static Specification<Avaliacao> comNotaGeralIgual(Integer nota) {
        return (root, query, cb) -> cb.equal(root.get("notaGeral"), nota);
    }

    public static Specification<Avaliacao> comNotaComidaIgual(Integer nota) {
        return (root, query, cb) -> cb.equal(root.get("notaComida"), nota);
    }

    public static Specification<Avaliacao> comNotaAmbienteIgual(Integer nota) {
        return (root, query, cb) -> cb.equal(root.get("notaAmbiente"), nota);
    }

    public static Specification<Avaliacao> comNotaAtendimentoIgual(Integer nota) {
        return (root, query, cb) -> cb.equal(root.get("notaAtendimento"), nota);
    }
}