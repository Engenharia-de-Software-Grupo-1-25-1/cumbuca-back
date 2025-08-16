package br.com.cumbuca.repository;

import br.com.cumbuca.model.UsuarioFavoritaEstabelecimento;
import br.com.cumbuca.model.UsuarioFavoritaEstabelecimentoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritoRepository extends JpaRepository<UsuarioFavoritaEstabelecimento, UsuarioFavoritaEstabelecimentoId> {
}