package br.com.cumbuca.dto.curtida;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Curtida;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurtidaResponseDTO {

    private UsuarioResponseDTO usuario;
    private AvaliacaoResponseDTO avaliacao;
    private boolean curtido;

    public CurtidaResponseDTO(Curtida curtida) {
        this.usuario = new UsuarioResponseDTO(curtida.getUsuario());
        this.avaliacao = new AvaliacaoResponseDTO(curtida.getAvaliacao());
    }
}
