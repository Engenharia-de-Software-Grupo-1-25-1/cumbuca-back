package br.com.cumbuca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.LocalDate;

@Data
@Entity
@Immutable
@Subselect("SELECT * FROM V_USUARIO")
public class UsuarioView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "DT_NASCIMENTO", nullable = false)
    private LocalDate dtNascimento;

    @Column(name = "FOTO")
    private byte[] foto;

    @Column(name = "STATUS")
    private String status = "ATIVO";

    @Column(name = "AVALIACOES")
    private int qtdAvaliacoes;
}
