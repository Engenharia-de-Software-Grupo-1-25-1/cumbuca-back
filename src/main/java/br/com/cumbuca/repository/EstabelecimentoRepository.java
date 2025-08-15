package br.com.cumbuca.repository;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResumoResponseDTO;
import br.com.cumbuca.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {
    @Query("SELECT new br.com.cumbuca.dto.estabelecimento.EstabelecimentoResumoResponseDTO( " +
            "e.id, e.nome, e.categoria, e.rua, e.numero, e.bairro, e.cidade, " +
            "AVG(a.notaGeral), COUNT(a.id), AVG(a.preco)) " +
            "FROM Estabelecimento e " +
            "LEFT JOIN Avaliacao a ON e.id = a.estabelecimento.id " +
            "GROUP BY e.id, e.nome, e.categoria, e.rua, e.numero, e.bairro, e.cidade")
    List<EstabelecimentoResumoResponseDTO> findEstabelecimentosSummary();
}