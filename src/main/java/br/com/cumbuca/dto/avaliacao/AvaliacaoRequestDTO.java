package br.com.cumbuca.dto.avaliacao;

import jakarta.validation.constraints.*;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AvaliacaoRequestDTO {

    @NotNull
    private Long idEstabelecimento;

    @NotBlank
    private String itemConsumido;

    @DecimalMin(value = "0.0")
    private BigDecimal preco;

    @NotBlank
    private String descricao;

    private List<String> tags;

    private List<MultipartFile> fotos;

    @Min(1) @Max(5)
    private Integer notaGeral;

    @Min(1) @Max(5)
    private Integer notaAmbiente;

    @Min(1) @Max(5)
    private Integer notaComida;

    @Min(1) @Max(5)
    private Integer notaAtendimento;
}