package br.com.cumbuca.controller;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.autenticacao.TokenService;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UsuarioControllerTest {
    final String URI = "/usuario";

    @Autowired
    MockMvc driver;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    UsuarioRequestDTO usuarioRequestDTO;
    Usuario usuario;

    private String token;

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
    class UsuarioFluxoBasicoApiRest {

        @Test
        void testCriarUsuario() throws Exception {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setEmail("criarjunit@email.com");
            dto.setSenha("123456");
            dto.setNome("Criar JUnit");
            dto.setUsername("criarjunit");
            dto.setDtNascimento(LocalDate.of(2000, 1, 1));
            MockMultipartFile foto = new MockMultipartFile(
                    "foto",
                    "perfil.jpg",
                    "image/jpeg",
                    "conteudo da foto".getBytes());


            final String responseJson = driver.perform(multipart(URI + "/criar")
                            .file(foto)
                            .param("email", dto.getEmail())
                            .param("senha", dto.getSenha())
                            .param("nome", dto.getNome())
                            .param("username", dto.getUsername())
                            .param("dtNascimento", dto.getDtNascimento().toString())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final UsuarioResponseDTO resultado = objectMapper.readValue(responseJson, UsuarioResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(dto.getNome(), resultado.getNome()),
                    () -> assertEquals(dto.getUsername(), resultado.getUsername()),
                    () -> assertEquals(dto.getEmail(), resultado.getEmail()),
                    () -> assertEquals(dto.getDtNascimento(), resultado.getDtNascimento())
            );
        }

        @Test
        void testAtualizarUsuario() throws Exception {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setEmail("atualizarjunit@email.com");
            dto.setSenha("123456");
            dto.setNome("Atualizar JUnit");
            dto.setUsername("atualizarjunit");
            dto.setDtNascimento(LocalDate.of(2000, 1, 1));

            final String responseJson = driver.perform(
                            multipart(URI + "/atualizar/" + usuario.getId())
                                    .param("email", dto.getEmail())
                                    .param("senha", dto.getSenha())
                                    .param("nome", dto.getNome())
                                    .param("username", dto.getUsername())
                                    .param("dtNascimento", dto.getDtNascimento().toString())
                                    .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                    })
                                    .header("Authorization", "Bearer " + token)
                                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final UsuarioResponseDTO resultado = objectMapper.readValue(responseJson, UsuarioResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(dto.getNome(), resultado.getNome()),
                    () -> assertEquals(dto.getUsername(), resultado.getUsername()),
                    () -> assertEquals(dto.getEmail(), resultado.getEmail()),
                    () -> assertEquals(dto.getDtNascimento(), resultado.getDtNascimento())
            );
        }

        @Test
        void testRemoverUsuario() throws Exception {
            driver.perform(delete(URI + "/remover/" + usuario.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isNoContent());
            assertFalse(usuarioRepository.findById(usuario.getId()).isPresent());
        }

        @Test
        void testRecuperarUsuarioPorId() throws Exception {
            String responseJson = driver.perform(get(URI + "/recuperar/" + usuario.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final UsuarioResponseDTO resultado = objectMapper.readValue(responseJson, UsuarioResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(usuario.getId(), resultado.getId()),
                    () -> assertEquals(usuario.getNome(), resultado.getNome()),
                    () -> assertEquals(usuario.getUsername(), resultado.getUsername()),
                    () -> assertEquals(usuario.getEmail(), resultado.getEmail()),
                    () -> assertEquals(usuario.getDtNascimento(), resultado.getDtNascimento())
            );
        }

        @Test
        void testRecuperarUsuarioPorUsername() throws Exception {
            String responseJson = driver.perform(get(URI + "/recuperar/" + usuario.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final UsuarioResponseDTO resultado = objectMapper.readValue(responseJson, UsuarioResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(usuario.getId(), resultado.getId()),
                    () -> assertEquals(usuario.getNome(), resultado.getNome()),
                    () -> assertEquals(usuario.getUsername(), resultado.getUsername()),
                    () -> assertEquals(usuario.getEmail(), resultado.getEmail()),
                    () -> assertEquals(usuario.getDtNascimento(), resultado.getDtNascimento())
            );
        }

        @Test
        void testListarUsuarios() throws Exception {
            Usuario usuario1 = new Usuario();
            usuario1.setEmail("listarjunit1@email.com");
            usuario1.setSenha(passwordEncoder.encode("123456"));
            usuario1.setNome("Listar JUnit 1");
            usuario1.setUsername("listarjunit1");
            usuario1.setDtNascimento(LocalDate.of(2000, 1, 1));

            Usuario usuario2 = new Usuario();
            usuario2.setEmail("listarjunit2@email.com");
            usuario2.setSenha(passwordEncoder.encode("123456"));
            usuario2.setNome("Listar JUnit 2");
            usuario2.setUsername("listarjunit2");
            usuario2.setDtNascimento(LocalDate.of(2000, 1, 1));

            usuarioRepository.saveAll(Arrays.asList(usuario1, usuario2));

            String responseJson = driver.perform(get(URI + "/listar")
                            .param("nome", "")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<UsuarioResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<List<UsuarioResponseDTO>>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getEmail().equals(usuario1.getEmail()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getEmail().equals(usuario2.getEmail())))
            );
        }


    }
}
