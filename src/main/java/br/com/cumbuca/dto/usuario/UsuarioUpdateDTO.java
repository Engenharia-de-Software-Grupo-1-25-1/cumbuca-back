package br.com.cumbuca.dto.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class UsuarioUpdateDTO {
    @NotBlank
    private String email;

    @NotBlank
    private  String nome;

    @NotBlank
    private String username;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dtNascimento;

    private  MultipartFile foto;

    private  String senhaAtual;
    private String novaSenha;
}
