package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;
import br.com.cumbuca.dto.tag.TagResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.*;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.ComentarioRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.comentario.ComentarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ServiceAndDtoTest {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    private Usuario usuario1;
    private Usuario usuario2;
    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario();
        usuario1.setEmail("bcn@gmail.com");
        usuario1.setSenha("123456");
        usuario1.setNome("Bê Cunha");
        usuario1.setUsername("Bernardo.bcn");
        usuario1.setDtNascimento(LocalDate.of(2010, 1, 2));
        usuarioRepository.save(usuario1);

        usuario2 = new Usuario();
        usuario2.setEmail("luciano.nascimento.filho@gmail.com");
        usuario2.setSenha("webhead");
        usuario2.setNome("Luciano Nascimento");
        usuario2.setUsername("Lulu Fazedor de Drift");
        usuario2.setDtNascimento(LocalDate.of(2001, 10, 24));
        usuarioRepository.save(usuario2);

        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);
        estabelecimento.setNome("O Gonzagão");
        estabelecimento.setCategoria("Restaurante");
        estabelecimentoRepository.save(estabelecimento);

        avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario1);
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setItemConsumido("Carne de Sol");
        avaliacao.setDescricao("Muito boa!");
        avaliacao.setPreco(BigDecimal.valueOf(50.00));
        avaliacao.setNotaGeral(5);
        avaliacaoRepository.save(avaliacao);
    }

    @Test
    @WithMockUser(username = "Bernardo.bcn")
    void testRecuperarComentarios() {
        comentarioService.comentar(avaliacao.getId(), "Comentário de teste");
        List<ComentarioResponseDTO> comentarios = comentarioService.recuperar(avaliacao.getId());
        assertFalse(comentarios.isEmpty());
        assertEquals(1, comentarios.size());
        assertEquals("Comentário de teste", comentarios.get(0).getComentario());
    }

    @Test
    @WithMockUser(username = "Bernardo.bcn")
    void testComentarAvaliacaoNaoEncontrada() {
        assertThrows(NoSuchElementException.class, () -> {
            comentarioService.comentar(999L, "Teste");
        });
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testRemoverComentarioSemPermissao() {
        ComentarioResponseDTO comentarioDTO = comentarioService.comentar(avaliacao.getId(), "Comentário do dono");
        Comentario comentarioSalvo = comentarioRepository.findById(comentarioDTO.getId()).orElseThrow();
        comentarioSalvo.setUsuario(usuario1);
        comentarioRepository.save(comentarioSalvo);

        assertThrows(CumbucaException.class, () -> {
            comentarioService.remover(comentarioSalvo.getId());
        });
    }

    @Test
    @WithMockUser(username = "Bernardo.bcn")
    void testRemoverComentarioNaoEncontrado() {
        assertThrows(NoSuchElementException.class, () -> {
            comentarioService.remover(999L);
        });
    }

    @Test
    void testCoverageDosDTOs() {
        Curtida curtida = new Curtida();
        curtida.setId(1L);
        curtida.setUsuario(usuario1);
        curtida.setAvaliacao(avaliacao);

        CurtidaResponseDTO curtidaDTO1 = new CurtidaResponseDTO();
        curtidaDTO1.setId(curtida.getId());
        curtidaDTO1.setUsuario(new UsuarioResponseDTO(usuario1));
        curtidaDTO1.setAvaliacao(new AvaliacaoResponseDTO(avaliacao));
        curtidaDTO1.setIsCurtida(true);

        CurtidaResponseDTO curtidaDTO2 = new CurtidaResponseDTO(curtida);
        curtidaDTO2.setIsCurtida(false);

        assertNotNull(curtidaDTO1.getId());
        assertNotNull(curtidaDTO1.getUsuario());
        assertNotNull(curtidaDTO1.getAvaliacao());
        assertTrue(curtidaDTO1.getIsCurtida());
        assertNotNull(curtidaDTO2.toString());

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setTag("barato");

        TagResponseDTO tagDTO1 = new TagResponseDTO();
        tagDTO1.setId(tag.getId());
        tagDTO1.setTag(tag.getTag());
        tagDTO1.setQuantidade(10);

        TagResponseDTO tagDTO2 = new TagResponseDTO(tag);
        assertNotNull(tagDTO1.getId());
        assertNotNull(tagDTO1.getTag());
        assertNotNull(tagDTO1.getQuantidade());
        assertNotNull(tagDTO2.toString());
        assertNotNull(new TagResponseDTO(new Tag()));
    }
}