package br.com.cumbuca.dto.recuperarSenha;

import lombok.Data;

@Data
public class AlterarSenhaRequestDTO {
    private String token;
    private String novaSenha;
    private String confirmarNovaSenha;

    public AlterarSenhaRequestDTO(String token, String novaSenha, String confirmarNovaSenha) {
        this.token = token;
        this.novaSenha = novaSenha;
        this.confirmarNovaSenha = confirmarNovaSenha;
    }
}