package br.com.cumbuca.dto.avaliacao;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import jakarta.validation.constraints.*;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AvaliacaoRequestDTO {

    @NotNull(message = "Informações do estabelecimento são obrigatórias")
    private EstabelecimentoRequestDTO estabelecimento;

    @NotBlank(message = "Item consumido é obrigatório")
    private String itemConsumido;

    @DecimalMin(value = "0.0", message = "Preço deve ser maior ou igual a zero")
    private BigDecimal preco;

    @NotBlank(message = "Descrição da avaliação é obrigatória")
    private String descricao;

    private List<String> tags;

    private List<MultipartFile> fotos;

    @Min(value = 1, message = "Nota geral deve ser entre 1 e 5")
    @Max(value = 5, message = "Nota geral deve ser entre 1 e 5")
    private Integer notaGeral;

    @Min(value = 1, message = "Nota do ambiente deve ser entre 1 e 5")
    @Max(value = 5, message = "Nota do ambiente deve ser entre 1 e 5")
    private Integer notaAmbiente;

    @Min(value = 1, message = "Nota da comida deve ser entre 1 e 5")
    @Max(value = 5, message = "Nota da comida deve ser entre 1 e 5")
    private Integer notaComida;

    @Min(value = 1, message = "Nota do atendimento deve ser entre 1 e 5")
    @Max(value = 5, message = "Nota do atendimento deve ser entre 1 e 5")
    private Integer notaAtendimento;
}