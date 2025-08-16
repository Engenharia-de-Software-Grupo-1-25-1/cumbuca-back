package br.com.cumbuca.dto.feed;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.model.Foto;
import lombok.Data;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Data
public class AvaliacaoFeedResponseDTO {
    private String nomeUsuario;
    private String fotoUsuario;
    private String nomeEstabelecimento;
    private Integer notaGeral;
    private String descricao;
    private int qtdCurtidas;
    private int qtdComentarios;
    private LocalDate data;
    private List<String> fotos;
    private List<String> tags;

    public AvaliacaoFeedResponseDTO(
            Avaliacao avaliacao,
            int qtdCurtidas,
            int qtdComentarios,
            List<Foto> fotos,
            List<Tag> tags
    ) {
        this.nomeUsuario = avaliacao.getUsuario().getNome();
        this.fotoUsuario = avaliacao.getUsuario().getFoto() != null
                ? Base64.getEncoder().encodeToString(avaliacao.getUsuario().getFoto())
                : null;
        this.nomeEstabelecimento = avaliacao.getEstabelecimento().getNome();
        this.notaGeral = avaliacao.getNotaGeral();
        this.descricao = avaliacao.getDescricao();
        this.data = avaliacao.getData();
        this.qtdCurtidas = qtdCurtidas;
        this.qtdComentarios = qtdComentarios;
        this.fotos = fotos.stream()
                .map(f -> Base64.getEncoder().encodeToString(f.getFoto()))
                .toList();
        this.tags = tags.stream()
                .map(Tag::getTag)
                .toList();
    }
}
