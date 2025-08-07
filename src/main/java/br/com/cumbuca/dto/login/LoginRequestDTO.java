package br.com.cumbuca.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    private String username;

    private String senha;
}
