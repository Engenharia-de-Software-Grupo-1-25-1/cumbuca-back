package br.com.cumbuca.dto.login;

import lombok.Getter;

@Getter
public class LoginResponseDTO {
    private String token;
    private Long id;

    public LoginResponseDTO(String token, Long id) {
        this.token = token;
        this.id = id;
    }
}

