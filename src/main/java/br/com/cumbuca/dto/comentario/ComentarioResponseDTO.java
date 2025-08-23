package br.com.cumbuca.dto.comentario;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Comentario;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComentarioResponseDTO {
    private Long id;
    private UsuarioResponseDTO usuario;
    private AvaliacaoResponseDTO avaliacao;
    private String comentario;

    public ComentarioResponseDTO(Comentario comentario) {
        this.id = comentario.getId();
        this.usuario = new UsuarioResponseDTO(comentario.getUsuario());
        this.avaliacao = new AvaliacaoResponseDTO(comentario.getAvaliacao());
        this.comentario = comentario.getComentario();
    }
}
