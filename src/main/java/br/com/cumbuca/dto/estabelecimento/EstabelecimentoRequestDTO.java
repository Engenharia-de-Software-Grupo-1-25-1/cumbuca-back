package br.com.cumbuca.dto.estabelecimento;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class EstabelecimentoRequestDTO {

    @NotBlank(message = "Nome do estabelecimento é obrigatório")
    private String nome;

    @NotBlank(message = "Categoria do estabelecimento é obrigatória")
    private String categoria;

    private String rua;

    private String numero;

    private String bairro;

    private String cidade;

    private String estado;

    private String cep;

    private List<String> horarios;
}
