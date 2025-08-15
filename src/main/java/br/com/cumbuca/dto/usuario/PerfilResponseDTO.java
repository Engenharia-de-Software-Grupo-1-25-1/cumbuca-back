package br.com.cumbuca.dto.usuario;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PerfilResponseDTO {
    @Setter
    @Getter
    private String nome;
    private String username;
    private String fotoPerfil;
    private List<AvaliacaoResponseDTO> avaliacaoResponseDTOList;
    private List<String> tagsPopulares;
    private boolean podeEditar;


    private List<AvaliacaoResponseDTO> avaliacoes;

    public PerfilResponseDTO(String nome, String username, byte[] fotoPerfil,
                             List<AvaliacaoResponseDTO> avaliacoes,
                             List<String> tagsPopulares, boolean podeEditar) {
        this.nome = nome;
        this.username = username;
        this.fotoPerfil = fotoPerfil;
        this.avaliacoes = avaliacoes;
        this.tagsPopulares = tagsPopulares;
        this.podeEditar = podeEditar;
    }

}
