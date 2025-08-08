package br.com.cumbuca.service.autenticacao;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface RecuperarSenhaService {
    void recuperarSenha(String email);
    void alterarSenha(String token, String novaSenha, String confirmarNovaSenha);
}