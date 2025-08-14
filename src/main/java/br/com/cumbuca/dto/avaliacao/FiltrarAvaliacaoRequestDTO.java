package br.com.cumbuca.dto.avaliacao;

import br.com.cumbuca.enums.avaliacao.FiltrosAvaliacao;
import br.com.cumbuca.enums.avaliacao.OrdenacaoAvaliacao;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FiltrarAvaliacaoRequestDTO {

    private FiltrosAvaliacao filtrarAvaliacao;

    @NotBlank(message = "Filtro é obrigatório")
    private String filtro;
    private OrdenacaoAvaliacao ordenacao;
}