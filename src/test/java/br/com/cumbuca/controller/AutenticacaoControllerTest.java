package br.com.cumbuca.controller;

import br.com.cumbuca.dto.login.LoginRequestDTO;
import br.com.cumbuca.dto.senha.AlterarSenhaRequestDTO;
import br.com.cumbuca.dto.senha.RecuperarSenhaRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.autenticacao.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AutenticacaoControllerTest {

    @Autowired
    MockMvc driver;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    ModelMapper modelMapper = new ModelMapper();
    UsuarioRequestDTO usuarioRequestDTO;
    Usuario usuario;
    String token;

    @BeforeEach
    void setup() {
        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setEmail("testejunit@email.com");
        usuarioRequestDTO.setSenha("123456");
        usuarioRequestDTO.setNome("Teste JUnit");
        usuarioRequestDTO.setUsername("testejunit");
        usuarioRequestDTO.setDtNascimento(LocalDate.of(2000, 1, 1));

        usuario = modelMapper.map(usuarioRequestDTO, Usuario.class);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        usuarioRepository.save(usuario);

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(usuario.getUsername(), usuarioRequestDTO.getSenha());
        final Authentication authentication = authenticationManager.authenticate(authToken);
        token = tokenService.gerarToken((Usuario) authentication.getPrincipal());
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Nested
    class AutenticacaoFluxoBasicoApiRest {

        @Test
        void testEfetuarLogin() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("testejunit");
            loginRequest.setSenha("123456");

            driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.id").value(usuario.getId()));
        }

        @Test
        void testEnviarEmailRecuperacaoSenha() throws Exception {
            final RecuperarSenhaRequestDTO recuperarSenhaRequest = new RecuperarSenhaRequestDTO();
            recuperarSenhaRequest.setEmail("testejunit@email.com");

            driver.perform(post("/recuperar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recuperarSenhaRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("E-mail enviado."));
        }

        @Test
        void testAlterarSenha() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("testejunit");
            loginRequest.setSenha("123456");

            final MvcResult result = driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            final String tokenRecebido = objectMapper.readTree(result.getResponse().getContentAsString())
                    .get("token").asText();

            final AlterarSenhaRequestDTO alterarSenhaRequest = new AlterarSenhaRequestDTO();
            alterarSenhaRequest.setToken(tokenRecebido);
            alterarSenhaRequest.setNovaSenha("novaSenha123");
            alterarSenhaRequest.setConfirmarNovaSenha("novaSenha123");

            driver.perform(post("/alterar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(alterarSenhaRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Senha alterada com sucesso."));
        }

    }

    @Nested
    class AutenticacaoLoginInconsistente {

        @Test
        void testEfetuarLoginUsernameNulo() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername(null);
            loginRequest.setSenha("123456");

            driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testEfetuarLoginSenhaNula() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("testejunit");
            loginRequest.setSenha(null);

            driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testEfetuarLoginUsarnameInvalido() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("nomeinvalido");
            loginRequest.setSenha("123456");

            driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void testEfetuarLoginSenhaInvalida() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("testejunit");
            loginRequest.setSenha("654321");

            driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class AutenticacaoRecuperacaoSenhaInconsistente {

        @Test
        void testEnviarEmailNulo() throws Exception {
            final RecuperarSenhaRequestDTO recuperarSenhaRequest = new RecuperarSenhaRequestDTO();
            recuperarSenhaRequest.setEmail(null);

            driver.perform(post("/recuperar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recuperarSenhaRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testEnviarEmailVazio() throws Exception {
            final RecuperarSenhaRequestDTO recuperarSenhaRequest = new RecuperarSenhaRequestDTO();
            recuperarSenhaRequest.setEmail("");

            driver.perform(post("/recuperar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recuperarSenhaRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testEnviarEmailInvalido() throws Exception {
            final RecuperarSenhaRequestDTO recuperarSenhaRequest = new RecuperarSenhaRequestDTO();
            recuperarSenhaRequest.setEmail("emailinvalido@email.com");

            driver.perform(post("/recuperar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recuperarSenhaRequest)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class AutenticacaoAlteracaoSenhaInconsistente {

        @Test
        void testAlterarSenhaNula() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("testejunit");
            loginRequest.setSenha("123456");

            final MvcResult result = driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            final String tokenRecebido = objectMapper.readTree(result.getResponse().getContentAsString())
                    .get("token").asText();

            final AlterarSenhaRequestDTO alterarSenhaRequest = new AlterarSenhaRequestDTO();
            alterarSenhaRequest.setToken(tokenRecebido);
            alterarSenhaRequest.setNovaSenha("novaSenha123");
            alterarSenhaRequest.setConfirmarNovaSenha(null);

            driver.perform(post("/alterar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(alterarSenhaRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testAlterarSenhaVazia() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("testejunit");
            loginRequest.setSenha("123456");

            final MvcResult result = driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            final String tokenRecebido = objectMapper.readTree(result.getResponse().getContentAsString())
                    .get("token").asText();

        final AlterarSenhaRequestDTO alterarSenhaRequest = new AlterarSenhaRequestDTO();
        alterarSenhaRequest.setToken(tokenRecebido);
        alterarSenhaRequest.setNovaSenha("");
        alterarSenhaRequest.setConfirmarNovaSenha("");

            driver.perform(post("/alterar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(alterarSenhaRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testAlterarSenhasDiferentes() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("testejunit");
            loginRequest.setSenha("123456");

            final MvcResult result = driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            final String tokenRecebido = objectMapper.readTree(result.getResponse().getContentAsString())
                    .get("token").asText();

            final AlterarSenhaRequestDTO alterarSenhaRequest = new AlterarSenhaRequestDTO();
            alterarSenhaRequest.setToken(tokenRecebido);
            alterarSenhaRequest.setNovaSenha("654321");
            alterarSenhaRequest.setConfirmarNovaSenha("6543210");

            driver.perform(post("/alterar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(alterarSenhaRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("As senhas não coincidem."));
        }


        @Test
        void testAlterarSenhaTamanhoInvalido() throws Exception {
            final LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername("testejunit");
            loginRequest.setSenha("123456");

            final MvcResult result = driver.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            final String tokenRecebido = objectMapper.readTree(result.getResponse().getContentAsString())
                    .get("token").asText();

            final AlterarSenhaRequestDTO alterarSenhaRequest = new AlterarSenhaRequestDTO();
            alterarSenhaRequest.setToken(tokenRecebido);
            alterarSenhaRequest.setNovaSenha("12345");
            alterarSenhaRequest.setConfirmarNovaSenha("12345");

            driver.perform(post("/alterar-senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(alterarSenhaRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
}