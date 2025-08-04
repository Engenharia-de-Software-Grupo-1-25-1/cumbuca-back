package br.com.cumbuca.service.autenticacao;

import br.com.cumbuca.model.Usuario;

public interface TokenService {
    String gerarToken(Usuario usuario);

    Long verificarToken(String token);
}
