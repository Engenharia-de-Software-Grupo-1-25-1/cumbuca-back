package br.com.cumbuca.dto.recuperarSenha;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecuperarSenhaRequestDTO {

    @NotBlank
    private String email;
}