package br.com.cumbuca.dto.recuperarSenha;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailRequestDTO {

    @NotBlank
    String destinatario;
    String assunto;
    String texto;
}