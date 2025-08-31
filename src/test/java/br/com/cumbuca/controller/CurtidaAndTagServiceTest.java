package br.com.cumbuca.controller;

import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.*;
import br.com.cumbuca.repository.*;
import br.com.cumbuca.service.comentario.ComentarioService;
import br.com.cumbuca.service.curtida.CurtidaService;
import br.com.cumbuca.service.tag.TagService;
import br.com.cumbuca.service.usuario.UsuarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CurtidaAndTagServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CurtidaService curtidaService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private CurtidaRepository curtidaRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ComentarioService comentarioService;

    @PersistenceContext
    private EntityManager entityManager;

    private Usuario usuarioDono;
    private Usuario outroUsuario;
    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {

        entityManager.createNativeQuery("TRUNCATE TABLE usuario, estabelecimento, avaliacao, tag, usuario_curte_avaliacao RESTART IDENTITY CASCADE").executeUpdate();

        usuarioDono = new Usuario();
        usuarioDono.setId(1L);
        usuarioDono.setEmail("luciano.nascimento.filho@gmail.com");
        usuarioDono.setSenha("webhead");
        usuarioDono.setNome("Luciano Nascimento");
        usuarioDono.setUsername("Lulu Fazedor de Drift");
        usuarioDono.setDtNascimento(LocalDate.of(2000, 10, 24));
        usuarioRepository.save(usuarioDono);

        outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setEmail("outro.user@test.com");
        outroUsuario.setSenha("123456");
        outroUsuario.setNome("Outro Test User");
        outroUsuario.setUsername("outro.user");
        outroUsuario.setDtNascimento(LocalDate.of(2001, 1, 1));
        usuarioRepository.save(outroUsuario);

        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);
        estabelecimento.setNome("Estabelecimento Teste Unificado");
        estabelecimento.setCategoria("Lanchonete");
        estabelecimentoRepository.save(estabelecimento);

        avaliacao = new Avaliacao();
        avaliacao.setId(1L);
        avaliacao.setUsuario(usuarioDono);
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setItemConsumido("X-Burger");
        avaliacao.setDescricao("Muito bom");
        avaliacao.setPreco(BigDecimal.valueOf(20.00));
        avaliacao.setNotaGeral(5);
        avaliacaoRepository.saveAndFlush(avaliacao);
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testCurtirEAvaliacaoNaoPossuiCurtida() {
        curtidaService.curtir(avaliacao.getId());
        assertEquals(1, curtidaRepository.count());
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testDescurtirAvaliacaoJaCurtida() {
        curtidaService.curtir(avaliacao.getId()); // Primeira curtida
        assertEquals(1, curtidaRepository.count());

        curtidaService.curtir(avaliacao.getId()); // Segunda chamada para descurtir
        assertEquals(0, curtidaRepository.count());
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testCurtirAvaliacaoInexistente() {
        assertThrows(NoSuchElementException.class, () -> curtidaService.curtir(9999L));
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testCriarTags() {
        List<String> novasTags = List.of("hamburguer", "bom-preco");
        tagService.criar(novasTags, avaliacao);

        assertEquals(2, tagRepository.count());
    }

    @Test
    void testRecuperarTags() {
        Tag tag1 = new Tag();
        tag1.setAvaliacao(avaliacao);
        tag1.setTag("saboroso");
        tagRepository.save(tag1);

        List<String> tagsRecuperadas = tagService.recuperar(avaliacao.getId());

        assertNotNull(tagsRecuperadas);
        assertEquals(1, tagsRecuperadas.size());
        assertEquals("saboroso", tagsRecuperadas.get(0));
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testRemoverTagComPermissao() {
        Tag tag = new Tag();
        tag.setAvaliacao(avaliacao);
        tag.setTag("para-remover");
        tagRepository.save(tag);
        assertEquals(1, tagRepository.count());

        tagService.remover(avaliacao.getId());

        assertEquals(0, tagRepository.count());
    }

    @Test
    @WithMockUser(username = "outro.user")
    void testRemoverTagSemPermissao() {
        Tag tag = new Tag();
        tag.setAvaliacao(avaliacao);
        tag.setTag("protegida");
        tagRepository.save(tag);

        assertDoesNotThrow(() -> tagService.remover(avaliacao.getId()));
        assertEquals(0, tagRepository.count());
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testRemoverTagInexistente() {
        assertDoesNotThrow(() -> tagService.remover(9999L));
    }

    @Test
    void testListarTags() {
        Tag tag1 = new Tag();
        tag1.setAvaliacao(avaliacao);
        tag1.setTag("saboroso");
        tagRepository.save(tag1);

        var tags = tagService.listar();

        assertFalse(tags.isEmpty());
        assertEquals(1, tags.size());
        assertEquals("saboroso", tags.get(0).getTag());
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testIsAvaliacaoCurtidaRetornaTrueQuandoUsuarioCurtiu() {
        curtidaService.curtir(avaliacao.getId());

        boolean curtida = curtidaService.isAvaliacaoCurtida(usuarioDono.getId(), avaliacao.getId());
        assertTrue(curtida);
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testIsAvaliacaoCurtidaRetornaFalseQuandoUsuarioNaoCurtiu() {

        boolean curtida = curtidaService.isAvaliacaoCurtida(usuarioDono.getId(), avaliacao.getId());
        assertFalse(curtida);
    }

    @Test
    @WithMockUser(username = "outro.user")
    void testIsAvaliacaoCurtidaRetornaFalseQuandoOutroUsuarioCurtiu() {
        curtidaService.curtir(avaliacao.getId());

        boolean curtida = curtidaService.isAvaliacaoCurtida(usuarioDono.getId(), avaliacao.getId());
        assertFalse(curtida);
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testIsAvaliacaoCurtida_DeveRetornarFalse_QuandoOutroUsuarioCurtiu() {
        Curtida outraCurtida = new Curtida();
        outraCurtida.setUsuario(outroUsuario);
        outraCurtida.setAvaliacao(avaliacao);
        curtidaRepository.saveAndFlush(outraCurtida);

        boolean curtida = curtidaService.isAvaliacaoCurtida(usuarioDono.getId(), avaliacao.getId());

        assertFalse(curtida);
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testIsAvaliacaoCurtida_DeveRetornarTrue_QuandoUsuarioLogadoCurtiu() {
        curtidaService.curtir(avaliacao.getId());

        boolean curtida = curtidaService.isAvaliacaoCurtida(usuarioDono.getId(), avaliacao.getId());

        assertTrue(curtida);
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testIsAvaliacaoCurtida_DeveRetornarFalse_QuandoUsuarioLogadoNaoCurtiu() {

        boolean curtida = curtidaService.isAvaliacaoCurtida(usuarioDono.getId(), avaliacao.getId());

        assertFalse(curtida);
    }

    @Test
    @WithMockUser(username = "outro.user")
    void testIsAvaliacaoCurtida_DeveRetornarFalse_QuandoApenasOutroUsuarioCurtiu() {
        Curtida curtidaDoDono = new Curtida();
        curtidaDoDono.setUsuario(usuarioDono);
        curtidaDoDono.setAvaliacao(avaliacao);
        curtidaRepository.saveAndFlush(curtidaDoDono);

        boolean curtida = curtidaService.isAvaliacaoCurtida(outroUsuario.getId(), avaliacao.getId());

        assertFalse(curtida);
    }
}