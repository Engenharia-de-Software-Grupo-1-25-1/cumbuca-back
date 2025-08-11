package br.com.cumbuca.dto.avaliacao;

import br.com.cumbuca.enums.FiltrarAvaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.model.Usuario;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class FiltrarAvaliacaoRequestDTO {

    private FiltrarAvaliacao filtrarAvaliacao;
}
