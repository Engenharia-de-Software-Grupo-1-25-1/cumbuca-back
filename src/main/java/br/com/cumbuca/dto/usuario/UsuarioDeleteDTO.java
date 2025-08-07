package br.com.cumbuca.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioDeleteDTO {
    @NotBlank
    private  String email;

    @NotBlank
    private  String senha;
}
