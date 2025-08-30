package br.com.cumbuca.repository;

import br.com.cumbuca.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritoRespository extends JpaRepository<Favorito, Long> {

    Favorito findByUsuarioIdAndEstabelecimentoId(Long usuarioId, Long estabelecimentoId);

    boolean existsByUsuarioIdAndEstabelecimentoId(Long usuarioId, Long estabelecimentoId);
}
