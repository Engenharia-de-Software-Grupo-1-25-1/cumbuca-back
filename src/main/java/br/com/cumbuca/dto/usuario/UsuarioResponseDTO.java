package br.com.cumbuca.dto.usuario;

import br.com.cumbuca.model.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Base64;

@Data
@NoArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String email;
    private String nome;
    private String username;
    private LocalDate dtNascimento;
    private String foto;
    private String status;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.username = usuario.getUsername();
        this.dtNascimento = usuario.getDtNascimento();
        this.foto = usuario.getFoto() != null ? Base64.getEncoder().encodeToString(usuario.getFoto()) : null;
        this.status = usuario.getStatus();
    }
}
