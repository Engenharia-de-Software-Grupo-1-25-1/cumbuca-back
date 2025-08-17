package br.com.cumbuca.dto.avaliacao;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AvaliacaoResponseDTO {
    private Long id;
    private UsuarioResponseDTO usuario;
    private EstabelecimentoResponseDTO estabelecimento;
    private String itemConsumido;
    private String descricao;
    private BigDecimal preco;
    private Integer notaGeral;
    private Integer notaComida;
    private Integer notaAtendimento;
    private Integer notaAmbiente;
    private LocalDate data;

    public AvaliacaoResponseDTO(Avaliacao avaliacao) {
        this.id = avaliacao.getId();
        this.usuario = new UsuarioResponseDTO(avaliacao.getUsuario());
        this.estabelecimento = new EstabelecimentoResponseDTO((avaliacao.getEstabelecimento()));
        this.itemConsumido = avaliacao.getItemConsumido();
        this.descricao = avaliacao.getDescricao();
        this.preco = avaliacao.getPreco();
        this.notaGeral = avaliacao.getNotaGeral();
        this.notaComida = avaliacao.getNotaComida();
        this.notaAtendimento = avaliacao.getNotaAtendimento();
        this.notaAmbiente = avaliacao.getNotaAmbiente();
        this.data = avaliacao.getData();
    }
}
