package br.com.cumbuca.dto.senha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlterarSenhaRequestDTO {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres")
    private String novaSenha;

    @NotBlank
    @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres")
    private String confirmarNovaSenha;

}