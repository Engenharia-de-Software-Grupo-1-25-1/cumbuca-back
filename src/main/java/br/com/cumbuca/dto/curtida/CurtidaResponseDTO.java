package br.com.cumbuca.dto.curtida;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Curtida;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurtidaResponseDTO {

    private Long id;
    private UsuarioResponseDTO usuario;
    private AvaliacaoResponseDTO avaliacao;
    private Boolean isCurtida;

    public CurtidaResponseDTO(Curtida curtida) {
        this.id = curtida.getId();
        this.usuario = new UsuarioResponseDTO(curtida.getUsuario());
        this.avaliacao = new AvaliacaoResponseDTO(curtida.getAvaliacao());
        this.isCurtida = null;
    }
}
