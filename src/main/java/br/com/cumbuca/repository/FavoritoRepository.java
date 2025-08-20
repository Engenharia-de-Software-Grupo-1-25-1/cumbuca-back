package br.com.cumbuca.repository;

import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.UsuarioFavoritaEstabelecimento;
import br.com.cumbuca.model.UsuarioFavoritaEstabelecimentoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritoRepository extends JpaRepository<Estabelecimento, Long> {
    void save(UsuarioFavoritaEstabelecimento novoFavorito);

    boolean existsById(UsuarioFavoritaEstabelecimentoId usuarioFavoritaEstabelecimentoId);
    void deleteById(UsuarioFavoritaEstabelecimentoId favoritoId);
}
