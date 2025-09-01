package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.comentario.ComentarioResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Comentario;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.ComentarioRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.autenticacao.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ComentarioControllerTest {

    @Autowired
    MockMvc driver;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    ObjectMapper objectMapper;

    ModelMapper modelMapper = new ModelMapper();
    Usuario usuario;
    Avaliacao avaliacao;
    Estabelecimento estabelecimento;
    UsuarioRequestDTO usuarioRequestDTO;
    AvaliacaoRequestDTO avaliacaoRequestDTO;
    EstabelecimentoRequestDTO estabelecimentoRequestDTO;
    String token;

    @BeforeEach
    void setUp() {
        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setEmail("testejunit@email.com");
        usuarioRequestDTO.setSenha("123456");
        usuarioRequestDTO.setNome("Teste JUnit");
        usuarioRequestDTO.setUsername("testejunit");
        usuarioRequestDTO.setDtNascimento(LocalDate.of(2000, 1, 1));

        usuario = modelMapper.map(usuarioRequestDTO, Usuario.class);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        usuarioRepository.save(usuario);

        estabelecimentoRequestDTO = new EstabelecimentoRequestDTO();
        estabelecimentoRequestDTO.setId(1L);
        estabelecimentoRequestDTO.setNome("Restaurante Teste");
        estabelecimentoRequestDTO.setCategoria("Restaurante");

        estabelecimento = modelMapper.map(estabelecimentoRequestDTO, Estabelecimento.class);
        estabelecimentoRepository.save(estabelecimento);

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                usuario.getUsername(), usuarioRequestDTO.getSenha());
        final Authentication authentication = authenticationManager.authenticate(authToken);
        token = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        avaliacaoRequestDTO = new AvaliacaoRequestDTO();
        avaliacaoRequestDTO.setItemConsumido("picado");
        avaliacaoRequestDTO.setDescricao("Muito bem servido");
        avaliacaoRequestDTO.setPreco(new BigDecimal("25.00"));
        avaliacao = modelMapper.map(avaliacaoRequestDTO, Avaliacao.class);
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setUsuario(usuario);
        avaliacaoRepository.save(avaliacao);
    }

    @AfterEach
    void tearDown() {
        comentarioRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Nested
    class ComentarioFluxoBasicoApiRest {
        @Test
        void testComentarAvaliacao() throws Exception {
            String comentario = "Gostei bastante da comida!";

            String responseJson = driver.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/avaliacao/comentar/" + avaliacao.getId())
                        .content(comentario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            final ComentarioResponseDTO resultado =
                    objectMapper.readValue(responseJson, ComentarioResponseDTO.class);
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(resultado.getComentario(), comentario),
                    () -> assertEquals(resultado.getAvaliacaoId(), avaliacao.getId())
            );
        }

        @Test
        void testRemoverComentario() throws Exception {
            Comentario comentario = new Comentario();
            comentario.setAvaliacao(avaliacao);
            comentario.setUsuario(usuario);
            comentario.setConteudo("comentario teste remoção");
            comentarioRepository.save(comentario);

           driver.perform(delete("/comentario/remover/" + comentario.getId())
                           .contentType(MediaType.APPLICATION_JSON)
                           .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertFalse(comentarioRepository.findById(comentario.getId()).isPresent());

        }

        @Test
        void testRecuperarComentario() throws Exception {

        }


//    @TestsNoContent());
//    @WithMockUser(username = "Lulu Fazedor de Drift")
//    void testRecuperarComentarios() {
//        comentarioService.comentar(avaliacao.getId(), "Comentário de teste");
//        List<ComentarioResponseDTO> comentarios = comentarioService.recuperar(avaliacao.getId());e Drift")
//        assertFalse(comentarios.isEmpty());
//        assertEquals(1, comentarios.size());   comentarioService.comentar(avaliacao.getId(), "Comentário de teste");
//        assertEquals("Comentário de teste", comentarios.get(0).getComentario());        List<ComentarioResponseDTO> comentarios = comentarioService.recuperar(avaliacao.getId());
//    }ssertFalse(comentarios.isEmpty());
//
//    @Testentarios.get(0).getComentario());
//    @WithMockUser(username = "Lulu Fazedor de Drift")
//    void testComentarAvaliacaoNaoEncontrada() {
//        assertThrows(NoSuchElementException.class, () ->
//            comentarioService.comentar(999L, "Teste")WithMockUser(username = "Lulu Fazedor de Drift")
//        );    void testComentarAvaliacaoNaoEncontrada() {
//    }ssertThrows(NoSuchElementException.class, () ->
//
//    @Test
//    @WithMockUser(username = "Lulu Fazedor de Drift")
//    void testRemoverComentarioNaoEncontrado() {
//        assertThrows(NoSuchElementException.class, () -> {
//            comentarioService.remover(999L);WithMockUser(username = "Lulu Fazedor de Drift")
//        });/    void testRemoverComentarioNaoEncontrado() {


}//    }//        assertThrows(NoSuchElementException.class, () -> {
//            comentarioService.remover(999L);
//        });
//    }
}