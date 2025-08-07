package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.model.*;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.service.estabelecimento.EstabelecimentoService;
import br.com.cumbuca.service.foto.FotoService;
import br.com.cumbuca.service.tag.TagService;
import br.com.cumbuca.service.usuario.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ModelMapper modelMapper;
    private final UsuarioService usuarioService;
    private final EstabelecimentoService estabelecimentoService;
    private final TagService tagService;
    private final FotoService fotoService;

    public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository, ModelMapper modelMapper,
            UsuarioService usuarioService, EstabelecimentoService estabelecimentoService, TagService tagService,
            FotoService fotoService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.modelMapper = modelMapper;
        this.usuarioService = usuarioService;
        this.estabelecimentoService = estabelecimentoService;
        this.tagService = tagService;
        this.fotoService = fotoService;
    }

    @Override
    @Transactional
    public Avaliacao criar(AvaliacaoRequestDTO avaliacaoRequestDTO) {
        Usuario usuario = usuarioService.getUsuarioLogado();

        modelMapper.typeMap(AvaliacaoRequestDTO.class, Avaliacao.class)
                .addMappings(mapper -> {
                    mapper.skip(Avaliacao::setUsuario);
                    mapper.skip(Avaliacao::setEstabelecimento);
                    mapper.skip(Avaliacao::setFotos);
                    mapper.skip(Avaliacao::setTags);
                });

        Avaliacao avaliacao = modelMapper.map(avaliacaoRequestDTO, Avaliacao.class);
        Estabelecimento estabelecimento = estabelecimentoService
                .buscaOuCria(avaliacaoRequestDTO.getEstabelecimentoRequestDTO());
        avaliacao.setUsuario(usuario);
        avaliacao.setEstabelecimento(estabelecimento);

        if (avaliacaoRequestDTO.getFotos() != null && !avaliacaoRequestDTO.getFotos().isEmpty()) {
            List<Foto> fotos = fotoService.gerarFotos(avaliacaoRequestDTO.getFotos(), avaliacao);
            avaliacao.setFotos(fotos);
        }

        if (avaliacaoRequestDTO.getTags() != null && !avaliacaoRequestDTO.getTags().isEmpty()) {
            List<Tag> tags = tagService.gerarTags(avaliacaoRequestDTO.getTags(), avaliacao);
            avaliacao.setTags(tags);
        }

        return avaliacaoRepository.save(avaliacao);
    }
}
