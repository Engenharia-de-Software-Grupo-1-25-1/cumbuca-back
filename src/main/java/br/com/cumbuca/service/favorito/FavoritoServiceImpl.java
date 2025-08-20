package br.com.cumbuca.service.favorito;

import br.com.cumbuca.dto.favorito.FavoritoResponseDTO;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.model.UsuarioFavoritaEstabelecimento;
import br.com.cumbuca.model.UsuarioFavoritaEstabelecimentoId;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.FavoritoRepository;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioService usuarioService;

    public FavoritoServiceImpl(FavoritoRepository favoritoRepository, EstabelecimentoRepository estabelecimentoRepository, UsuarioService usuarioService) {
        this.favoritoRepository = favoritoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public FavoritoResponseDTO favoritar(Long idEstabelecimento) {
        final Usuario usuario = usuarioService.getUsuarioLogado();
        final Estabelecimento estabelecimento = estabelecimentoRepository.findById(idEstabelecimento)
                .orElseThrow(() -> new NoSuchElementException("Estabelecimento não encontrado."));

        final UsuarioFavoritaEstabelecimentoId favoritoId = new UsuarioFavoritaEstabelecimentoId(usuario.getId(), estabelecimento.getId());
        final boolean isFavorito = favoritoRepository.existsById(favoritoId);

        if (isFavorito) {
            favoritoRepository.deleteById(favoritoId);
            return new FavoritoResponseDTO(false, "Estabelecimento removido dos favoritos.");
        } else {
            final UsuarioFavoritaEstabelecimento novoFavorito = new UsuarioFavoritaEstabelecimento();
            novoFavorito.setId(favoritoId);
            favoritoRepository.save(novoFavorito);
            return new FavoritoResponseDTO(true, "Estabelecimento adicionado aos favoritos.");
        }
    }
}