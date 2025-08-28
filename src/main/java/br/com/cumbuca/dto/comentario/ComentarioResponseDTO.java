package br.com.cumbuca.dto.comentario;

import br.com.cumbuca.model.Comentario;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComentarioResponseDTO {
    private Long id;
    private Long idUsuario;
    private Long idAvaliacao;
    private String comentario;

    public ComentarioResponseDTO(Comentario comentario) {
        this.id = comentario.getId();
        this.idUsuario = comentario.getUsuario().getId();
        this.idAvaliacao = comentario.getAvaliacao().getId();
        this.comentario = comentario.getComentario();
    }
}
