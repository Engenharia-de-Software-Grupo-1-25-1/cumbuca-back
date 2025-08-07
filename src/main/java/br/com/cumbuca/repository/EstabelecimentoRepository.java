package br.com.cumbuca.repository;

import br.com.cumbuca.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    Optional<Estabelecimento> findByNomeAndRuaAndCidadeAndEstadoAndCep(
            String nome,
            String rua,
            String cidade,
            String estado,
            String cep);
}
