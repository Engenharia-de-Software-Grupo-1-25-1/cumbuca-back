package br.com.cumbuca.dto.usuario;

import br.com.cumbuca.model.Usuario;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String email;
    private String nome;
    private String username;
    private LocalDate dtNascimento;
    private byte[] foto;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.username = usuario.getUsername();
        this.dtNascimento = usuario.getDtNascimento();
        this.foto = usuario.getFoto();
    }
}
