package br.com.cumbuca.controller;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.TagRepository;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.tag.TagService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "Lulu Fazedor de Drift")
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    @PersistenceContext
    private EntityManager entityManager;

    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {

        entityManager.createNativeQuery("TRUNCATE TABLE usuario, estabelecimento, avaliacao, tag, usuario_curte_avaliacao RESTART IDENTITY CASCADE").executeUpdate();

        final Usuario usuarioDono = new Usuario();
        usuarioDono.setId(1L);
        usuarioDono.setEmail("luciano.nascimento.filho@gmail.com");
        usuarioDono.setSenha("webhead");
        usuarioDono.setNome("Luciano Nascimento");
        usuarioDono.setUsername("Lulu Fazedor de Drift");
        usuarioDono.setDtNascimento(LocalDate.of(2000, 10, 24));
        usuarioRepository.save(usuarioDono);

        final Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);
        estabelecimento.setNome("Test Estabelecimento");
        estabelecimento.setCategoria("Restaurante");
        estabelecimentoRepository.save(estabelecimento);

        avaliacao = new Avaliacao();
        avaliacao.setId(1L);
        avaliacao.setUsuario(usuarioDono);
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setItemConsumido("Pizza");
        avaliacao.setDescricao("Muito boa!");
        avaliacao.setPreco(BigDecimal.valueOf(50.00));
        avaliacao.setNotaGeral(5);
        avaliacaoRepository.save(avaliacao);

        final Tag tag1 = new Tag();
        tag1.setAvaliacao(avaliacao);
        tag1.setTag("bom");
        tagRepository.save(tag1);

        final Tag tag2 = new Tag();
        tag2.setAvaliacao(avaliacao);
        tag2.setTag("barato");
        tagRepository.save(tag2);
    }

    @AfterEach
    void tearDown() {
        tagRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testListarTags() throws Exception {
        mockMvc.perform(get("/tag/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testListarTagsPopulares() throws Exception {
        mockMvc.perform(get("/tag/populares/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
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
}