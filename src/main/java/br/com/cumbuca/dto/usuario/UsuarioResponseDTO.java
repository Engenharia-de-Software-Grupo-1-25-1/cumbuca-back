package br.com.cumbuca.dto.usuario;

import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.model.UsuarioView;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String email;
    private String nome;
    private String username;
    private LocalDate dtNascimento;
    private byte[] foto;
    private String status;
    private int qtdAvaliacoes;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.username = usuario.getUsername();
        this.dtNascimento = usuario.getDtNascimento();
        this.foto = usuario.getFoto();
        this.status = usuario.getStatus();
        this.qtdAvaliacoes = 0;
    }

    public UsuarioResponseDTO(UsuarioView usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.username = usuario.getUsername();
        this.dtNascimento = usuario.getDtNascimento();
        this.foto = usuario.getFoto();
        this.status = usuario.getStatus();
        this.qtdAvaliacoes = usuario.getQtdAvaliacoes();
    }
}
