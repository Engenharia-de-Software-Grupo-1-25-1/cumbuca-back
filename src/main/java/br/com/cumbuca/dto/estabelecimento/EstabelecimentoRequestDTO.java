package br.com.cumbuca.dto.estabelecimento;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EstabelecimentoRequestDTO {

    private Long id;

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
}
