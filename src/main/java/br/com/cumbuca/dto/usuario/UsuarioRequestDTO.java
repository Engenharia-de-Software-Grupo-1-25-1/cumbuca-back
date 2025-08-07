package br.com.cumbuca.dto.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UsuarioRequestDTO {

    private String email;

    private String senha;

    private String nome;

    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dtNascimento;

    private MultipartFile foto;
}
