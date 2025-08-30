package br.com.cumbuca.dto.avaliacao;

import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.AvaliacaoView;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
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
    private List<String> fotos;
    private List<String> tags;
    private boolean isCurtida;
    private Integer qtdCurtidas;
    private Integer qtdComentarios;
    private List<ComentarioResponseDTO> comentarios;

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
        this.isCurtida = false;
        this.qtdCurtidas = 0;
        this.qtdComentarios = 0;
        this.comentarios = new ArrayList<>();
    }

    public AvaliacaoResponseDTO(AvaliacaoView avaliacao) {
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
        this.isCurtida = false;
        this.qtdCurtidas = avaliacao.getQtdCurtidas();
        this.qtdComentarios = avaliacao.getQtdComentarios();
        this.comentarios = new ArrayList<>();
    }
}
