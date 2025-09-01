package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.curtida.CurtidaResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.model.*;
import br.com.cumbuca.repository.*;
import br.com.cumbuca.service.autenticacao.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CurtidaControllerTest {

    @Autowired
    MockMvc driver;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private CurtidaRepository curtidaRepository;

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
        curtidaRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Nested
    class CurtidaFluxoBasicoApiRest {
        @Test
        void testCurtirAvaliacao() throws Exception {
            String responseJson = driver.perform(
                            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                    .post("/avaliacao/curtir/" + avaliacao.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + token)
                                    .characterEncoding("UTF-8"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            final CurtidaResponseDTO resposta = objectMapper.readValue(responseJson, CurtidaResponseDTO.class);
            assertAll(
                    () -> assertTrue(resposta.getIsCurtida())
            );

        }

        @Test
        void testDescurtirAvaliacao() throws Exception {
            Curtida curtida = new Curtida();
            curtida.setAvaliacao(avaliacao);
            curtida.setUsuario(usuario);
            curtidaRepository.save(curtida);
            String responseJson = driver.perform(
                            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                    .post("/avaliacao/curtir/" + avaliacao.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + token)
                                    .characterEncoding("UTF-8"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            final CurtidaResponseDTO resposta = objectMapper.readValue(responseJson, CurtidaResponseDTO.class);
            assertAll(
                    () -> assertFalse(resposta.getIsCurtida())
            );
        }

//        @Test
//        void testDescurtirAvaliacaoOutroUsuario() throws Exception {
//            UsuarioRequestDTO usuarioRequestDTO2 = new UsuarioRequestDTO();
//            usuarioRequestDTO2.setEmail("testejunit2@email.com");
//            usuarioRequestDTO2.setSenha("123456");
//            usuarioRequestDTO2.setNome("Teste JUnit2");
//            usuarioRequestDTO2.setUsername("testejunit2");
//            usuarioRequestDTO2.setDtNascimento(LocalDate.of(2000, 1, 1));
//            Usuario usuario2 = modelMapper.map(usuarioRequestDTO2, Usuario.class);
//            usuario2.setSenha(passwordEncoder.encode(usuarioRequestDTO2.getSenha()));
//            usuarioRepository.save(usuario2);
//
//            // salva curtida feita pelo usuario2
//            Curtida curtida = new Curtida();
//            curtida.setAvaliacao(avaliacao);
//            curtida.setUsuario(usuario2);
//            curtidaRepository.save(curtida);
//
//            // tenta descurtir com usuario1 (token do usuario1)
//            String responseText = driver.perform(
//                            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//                                    .post("/avaliacao/curtir/" + avaliacao.getId())
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .header("Authorization", "Bearer " + token) // token do usuário1
//                                    .characterEncoding("UTF-8"))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andReturn()
//                    .getResponse()
//                    .getContentAsString(StandardCharsets.UTF_8);
//
//            assertEquals("Usuário não tem permissão para realizar esta ação.", responseText);
//        }

    }
}





