package br.com.cumbuca.service.autenticacao;

public interface RecuperarSenhaService {
    void recuperarSenha(String email);
    void alterarSenha(String token, String novaSenha, String confirmarNovaSenha);
}