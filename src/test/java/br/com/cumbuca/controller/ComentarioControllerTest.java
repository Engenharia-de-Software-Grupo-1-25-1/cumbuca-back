package br.com.cumbuca.controller;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Comentario;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.ComentarioRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.UsuarioRepository;
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

    private Usuario usuario;
    private Avaliacao avaliacao;
    private Estabelecimento estabelecimento;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("luciano.nascimento.filho@gmail.com");
        usuario.setSenha("webhead");
        usuario.setNome("Luciano Nascimento");
        usuario.setUsername("Lulu Fazedor de Drift");
        usuario.setDtNascimento(LocalDate.of(2001, 10, 24));
        usuarioRepository.save(usuario);

        estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);
        estabelecimento.setNome("O Gonzagão");
        estabelecimento.setCategoria("Restaurante");
        estabelecimentoRepository.save(estabelecimento);

        avaliacao = new Avaliacao();
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
}