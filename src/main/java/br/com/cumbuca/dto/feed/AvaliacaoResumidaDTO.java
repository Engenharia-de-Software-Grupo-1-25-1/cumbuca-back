package br.com.cumbuca.dto.feed;

import java.time.LocalDate;
import java.util.List;

public class AvaliacaoResumidaDTO {
    private String nomeUsuario;
    private String fotoUsuarioBase64;
    private String nomeEstabelecimento;
    private Integer notaGeral;
    private String descricao;
    private int qtdCurtidas;
    private int qtdComentarios;
    private List<String> fotosBase64;
    private List<String> tags;
    private LocalDate data;

    public AvaliacaoResumidaDTO(String nomeUsuario, String fotoUsuarioBase64, String nomeEstabelecimento,
                                Integer notaGeral, String descricao, int qtdCurtidas, int qtdComentarios,
                                List<String> fotosBase64, List<String> tags, LocalDate data) {
        this.nomeUsuario = nomeUsuario;
        this.fotoUsuarioBase64 = fotoUsuarioBase64;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.notaGeral = notaGeral;
        this.descricao = descricao;
        this.qtdCurtidas = qtdCurtidas;
        this.qtdComentarios = qtdComentarios;
        this.fotosBase64 = fotosBase64;
        this.tags = tags;
        this.data = data;
    }
}