package br.com.cumbuca.dto.avaliacao;

import br.com.cumbuca.exception.CumbucaException;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AvaliacaoFiltroRequestDTO {

    private String usuario;
    private String estabelecimento;
    private String itemConsumido;
    private List<String> tags;
    private BigDecimal precoInicio;
    private BigDecimal precoFim;
    private Integer notaGeral;
    private Integer notaComida;
    private Integer notaAmbiente;
    private Integer notaAtendimento;

    public void validarIntervaloPreco() {
        if (precoInicio != null && precoFim != null && precoInicio.compareTo(precoFim) > 0) {
            throw new CumbucaException("Preço de início não pode ser maior que o preço final");
        }
    }
}