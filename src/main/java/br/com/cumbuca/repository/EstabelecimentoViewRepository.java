package br.com.cumbuca.repository;

import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.EstabelecimentoView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstabelecimentoViewRepository extends JpaRepository<EstabelecimentoView, Long> {
    List<EstabelecimentoView> findAll();

    Estabelecimento findByNomeNormalizado(String nome);
}
