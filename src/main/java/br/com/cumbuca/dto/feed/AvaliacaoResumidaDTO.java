package br.com.cumbuca.dto.feed;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Tag;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AvaliacaoResumidaDTO {
    private String nomeUsuario;
    private String fotoUsuario;
    private String nomeEstabelecimento;
    private Integer notaGeral;
    private String descricao;
    private int qtdCurtidas;
    private int qtdComentarios;
    private List<String> fotos;
    private List<String> tags;
    private LocalDate data;

    public AvaliacaoResumidaDTO(Avaliacao avaliacao, int qtdCurtidas, int qtdComentarios) {
        this.nomeUsuario = avaliacao.getUsuario().getNome();
        this.fotoUsuario = avaliacao.getUsuario().getFoto() != null ? Base64.getEncoder().encodeToString(avaliacao.getUsuario().getFoto()) : null;
        this.nomeEstabelecimento = avaliacao.getEstabelecimento().getNome();
        this.notaGeral = avaliacao.getNotaGeral();
        this.descricao = avaliacao.getDescricao();
        this.qtdCurtidas = qtdCurtidas;
        this.qtdComentarios = qtdComentarios;
        this.fotos = avaliacao.getFotos() != null
                ? avaliacao.getFotos().stream()
                    .map(f -> Base64.getEncoder().encodeToString(f.getFoto()))
                    .collect(Collectors.toList())
                : new ArrayList<>();
        this.tags = avaliacao.getTags() != null
                ? avaliacao.getTags().stream()
                    .map(Tag::getTag)
                    .collect(Collectors.toList())
                : new ArrayList<>();
        this.data = avaliacao.getData();
    }
}
