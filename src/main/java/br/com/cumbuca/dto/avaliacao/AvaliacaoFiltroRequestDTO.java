package br.com.cumbuca.dto.avaliacao;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AvaliacaoFiltroRequestDTO {

    private String usuario;
    private String estabelecimento;
    private String itemConsumido;
    private List<String> tags;
    private BigDecimal precoMinimo;
    private BigDecimal precoMaximo;
    private Integer notaGeral;
    private Integer notaComida;
    private Integer notaAmbiente;
    private Integer notaAtendimento;
}