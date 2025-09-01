package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;
import br.com.cumbuca.dto.tag.TagResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.*;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.CurtidaRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.curtida.CurtidaService;
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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "Lulu Fazedor de Drift")
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

    @Autowired
    private CurtidaService curtidaService;

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
                .andExpect(jsonPath("$.isCurtida").value(true));
    }

    @Test
    void testCoverageDosDTOs() {
        Curtida curtida = new Curtida();
        curtida.setId(1L);
        curtida.setUsuario(usuarioDono);
        curtida.setAvaliacao(avaliacao);

        CurtidaResponseDTO curtidaDTO1 = new CurtidaResponseDTO();
        curtidaDTO1.setId(curtida.getId());
        curtidaDTO1.setUsuario(new UsuarioResponseDTO(usuarioDono));
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