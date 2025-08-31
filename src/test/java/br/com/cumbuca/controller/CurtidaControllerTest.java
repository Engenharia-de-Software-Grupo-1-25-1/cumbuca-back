package br.com.cumbuca.controller;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.CurtidaRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CurtidaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private CurtidaRepository curtidaRepository;

    private Usuario usuario;
    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
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

        avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setItemConsumido("Pizza");
        avaliacao.setDescricao("Muito boa!");
        avaliacao.setPreco(BigDecimal.valueOf(50.00));
        avaliacao.setNotaGeral(5);
        avaliacaoRepository.save(avaliacao);
    }

    @AfterEach
    void tearDown() {
        curtidaRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testCurtir() throws Exception {
        mockMvc.perform(post("/avaliacao/curtir/{avaliacaoId}", avaliacao.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.curtido").value(true));
    }
}