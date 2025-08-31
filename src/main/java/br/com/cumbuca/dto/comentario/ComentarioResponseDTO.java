package br.com.cumbuca.dto.comentario;

import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Comentario;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComentarioResponseDTO {
    private Long id;
    private UsuarioResponseDTO usuario;
    private Long avaliacaoId;
    private String comentario;

    public ComentarioResponseDTO(Comentario comentario) {
        this.id = comentario.getId();
        this.usuario = new UsuarioResponseDTO(comentario.getUsuario());
        this.avaliacaoId = comentario.getAvaliacao().getId();
        this.comentario = comentario.getConteudo();
    }
}
