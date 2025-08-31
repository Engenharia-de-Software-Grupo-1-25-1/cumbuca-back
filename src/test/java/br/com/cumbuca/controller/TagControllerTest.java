package br.com.cumbuca.controller;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.TagRepository;
import br.com.cumbuca.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
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

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@email.com");
        usuario.setSenha("123456");
        usuario.setNome("Test User");
        usuario.setUsername("testuser");
        usuario.setDtNascimento(LocalDate.of(2000, 1, 1));
        usuarioRepository.save(usuario);

        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setNome("Test Estabelecimento");
        estabelecimento.setCategoria("Restaurante");
        estabelecimentoRepository.save(estabelecimento);

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setItemConsumido("Pizza");
        avaliacao.setDescricao("Muito boa!");
        avaliacao.setPreco(BigDecimal.valueOf(50.00));
        avaliacao.setNotaGeral(5);
        avaliacaoRepository.save(avaliacao);

        Tag tag1 = new Tag();
        tag1.setAvaliacao(avaliacao);
        tag1.setTag("bom");
        tagRepository.save(tag1);

        Tag tag2 = new Tag();
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
}