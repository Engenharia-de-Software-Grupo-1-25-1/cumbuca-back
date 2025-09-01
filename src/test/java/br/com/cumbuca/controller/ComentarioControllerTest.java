package br.com.cumbuca.controller;

import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Comentario;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.ComentarioRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.comentario.ComentarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "Lulu Fazedor de Drift")
public class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ComentarioService comentarioService;

    @PersistenceContext
    private EntityManager entityManager;

    private Usuario usuario;
    private Usuario usuario2;
    private Avaliacao avaliacao;
    private Estabelecimento estabelecimento;

    @BeforeEach
    void setUp() {

        entityManager.createNativeQuery("TRUNCATE TABLE usuario, estabelecimento, avaliacao, tag, usuario_curte_avaliacao RESTART IDENTITY CASCADE").executeUpdate();


        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("luciano.nascimento.filho@gmail.com");
        usuario.setSenha("webhead");
        usuario.setNome("Luciano Nascimento");
        usuario.setUsername("Lulu Fazedor de Drift");
        usuario.setDtNascimento(LocalDate.of(2001, 10, 24));
        usuarioRepository.save(usuario);

        usuario2 = new Usuario();
        usuario2.setEmail("luciano.filho@gmail.com");
        usuario2.setSenha("alauae");
        usuario2.setNome("Tetse");
        usuario2.setUsername("outro.user");
        usuario2.setDtNascimento(LocalDate.of(2001, 11, 01));
        usuarioRepository.save(usuario2);


        estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);
        estabelecimento.setNome("O Gonzagão");
        estabelecimento.setCategoria("Restaurante");
        estabelecimentoRepository.save(estabelecimento);

        avaliacao = new Avaliacao();
        avaliacao.setId(1L);
        avaliacao.setUsuario(usuario);
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setItemConsumido("Frango à Parmegiana");
        avaliacao.setDescricao("Muito boa!");
        avaliacao.setPreco(BigDecimal.valueOf(50.00));
        avaliacao.setNotaGeral(5);
        avaliacaoRepository.save(avaliacao);
    }

    @AfterEach
    void tearDown() {
        comentarioRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testComentar() throws Exception {
        final String comentarioTexto = "Ótimo lugar!";

        mockMvc.perform(post("/avaliacao/comentar/{avaliacaoId}", avaliacao.getId())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(comentarioTexto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentario").value(comentarioTexto));
    }

    @Test
    void testRemoverComentario() throws Exception {
        final Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setAvaliacao(avaliacao);
        comentario.setComentario("Comentário para remover");
        comentarioRepository.save(comentario);

        mockMvc.perform(delete("/comentario/remover/{id}", comentario.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testRecuperarComentarios() {
        comentarioService.comentar(avaliacao.getId(), "Comentário de teste");
        List<ComentarioResponseDTO> comentarios = comentarioService.recuperar(avaliacao.getId());
        assertFalse(comentarios.isEmpty());
        assertEquals(1, comentarios.size());
        assertEquals("Comentário de teste", comentarios.get(0).getComentario());
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testComentarAvaliacaoNaoEncontrada() {
        assertThrows(NoSuchElementException.class, () -> {
            comentarioService.comentar(999L, "Teste");
        });
    }

    @Test
    @WithMockUser(username = "outro.user")
    void testRemoverComentarioSemPermissao() {
        ComentarioResponseDTO comentarioDTO = comentarioService.comentar(avaliacao.getId(), "Comentário do dono");
        Comentario comentarioSalvo = comentarioRepository.findById(comentarioDTO.getId()).orElseThrow();
        comentarioSalvo.setUsuario(usuario);
        comentarioRepository.save(comentarioSalvo);

        assertThrows(CumbucaException.class, () -> {
            comentarioService.remover(comentarioSalvo.getId());
        });
    }

    @Test
    @WithMockUser(username = "Lulu Fazedor de Drift")
    void testRemoverComentarioNaoEncontrado() {
        assertThrows(NoSuchElementException.class, () -> {
            comentarioService.remover(999L);
        });
    }
}