package br.com.cumbuca.dto.avaliacao;

import br.com.cumbuca.enums.FiltrosAvaliacao;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FiltrarAvaliacaoRequestDTO {

    private FiltrosAvaliacao filtrarAvaliacao;

    @NotBlank(message = "Filtro é obrigatório")
    private String filtro;
}
