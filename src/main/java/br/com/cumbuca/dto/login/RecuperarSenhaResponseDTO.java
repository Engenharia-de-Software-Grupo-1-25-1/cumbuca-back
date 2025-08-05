package br.com.cumbuca.dto.login;

import lombok.Data;

@Data
public class RecuperarSenhaResponseDTO {
    private String token;
    private String novaSenha;
    private String confirmarSenha;

    public RecuperarSenhaResponseDTO(String token, String novaSenha, String confirmarSenha) {
        this.token = token;
        this.novaSenha = novaSenha;
        this.confirmarSenha = confirmarSenha;
    }
}