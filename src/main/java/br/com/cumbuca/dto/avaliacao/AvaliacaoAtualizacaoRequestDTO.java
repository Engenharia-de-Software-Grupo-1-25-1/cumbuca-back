package br.com.cumbuca.dto.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AvaliacaoAtualizacaoRequestDTO {

    @NotBlank
    private String consumo;

    @NotBlank
    private String descricao;

    @Min(1) @Max(5) @NotNull
    private Integer notaGeral;

    @Min(1) @Max(5)
    private Integer notaComida;

    @Min(1) @Max(5)
    private Integer notaAmbiente;

    @Min(1) @Max(5)
    private Integer notaAtendimento;



    private BigDecimal preco;
    private List<MultipartFile> fotos;
    private List<String> tags;
}