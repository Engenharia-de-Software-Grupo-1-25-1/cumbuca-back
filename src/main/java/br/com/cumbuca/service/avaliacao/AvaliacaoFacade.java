package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.service.comentario.ComentarioService;
import br.com.cumbuca.service.curtida.CurtidaService;
import br.com.cumbuca.service.foto.FotoService;
import br.com.cumbuca.service.tag.TagService;
import org.springframework.stereotype.Service;

@Service
public class AvaliacaoFacade {
    private final FotoService fotoService;
    private final TagService tagService;
    private final ComentarioService comentarioService;
    private final CurtidaService curtidaService;

    public AvaliacaoFacade(FotoService fotoService, TagService tagService, ComentarioService comentarioService, CurtidaService curtidaService) {
        this.fotoService = fotoService;
        this.tagService = tagService;
        this.comentarioService = comentarioService;
        this.curtidaService = curtidaService;
    }

    public void montarDTOListar(AvaliacaoResponseDTO avaliacaoResponseDTO, Long avaliacaoId, Long usuarioId) {
        avaliacaoResponseDTO.setFotos(fotoService.recuperar(avaliacaoId));
        avaliacaoResponseDTO.setTags(tagService.recuperar(avaliacaoId));
        avaliacaoResponseDTO.setComentarios(comentarioService.recuperar(avaliacaoId));
        avaliacaoResponseDTO.setIsCurtida(curtidaService.isAvaliacaoCurtida(usuarioId, avaliacaoId));
    }

    public void montarDTORecuperar(AvaliacaoResponseDTO avaliacaoResponseDTO, Long avaliacaoId, Long usuarioId) {
        avaliacaoResponseDTO.setFotos(fotoService.recuperar(avaliacaoId));
        avaliacaoResponseDTO.setTags(tagService.recuperar(avaliacaoId));
        avaliacaoResponseDTO.setIsCurtida(curtidaService.isAvaliacaoCurtida(usuarioId, avaliacaoId));
    }
}

