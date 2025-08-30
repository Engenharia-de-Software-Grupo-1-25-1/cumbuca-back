package br.com.cumbuca.repository;

import br.com.cumbuca.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    Estabelecimento findByNomeAndCategoria(String nome, String categoria);
}