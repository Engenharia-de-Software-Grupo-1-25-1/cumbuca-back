package br.com.cumbuca.dto.senha;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecuperarSenhaRequestDTO {

    @NotBlank
    private String email;
}