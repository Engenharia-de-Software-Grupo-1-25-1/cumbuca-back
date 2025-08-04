package br.com.cumbuca.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String senha;
}
