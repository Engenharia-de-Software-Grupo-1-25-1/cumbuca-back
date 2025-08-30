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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    class UsuarioFluxoBasicoApiRest {

        @Test
        void testCriarUsuario() throws Exception {
            final UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setEmail("criarJunit@email.com");
            dto.setSenha("123456");
            dto.setNome("Criar JUnit");
            dto.setUsername("criarJunit");
            dto.setDtNascimento(LocalDate.of(2000, 1, 1));
            final MockMultipartFile foto = new MockMultipartFile(
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
            final UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setEmail("atualizarJunit@email.com");
            dto.setSenha("123456");
            dto.setNome("Atualizar JUnit");
            dto.setUsername("atualizarJunit");
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
            final String responseJson = driver.perform(get(URI + "/recuperar/" + usuario.getId())
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
            final String responseJson = driver.perform(get(URI + "/recuperar/" + usuario.getUsername())
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
            final Usuario usuario1 = new Usuario();
            usuario1.setEmail("listarJunit1@email.com");
            usuario1.setSenha(passwordEncoder.encode("123456"));
            usuario1.setNome("Listar JUnit 1");
            usuario1.setUsername("listarJunit1");
            usuario1.setDtNascimento(LocalDate.of(2000, 1, 1));

            final Usuario usuario2 = new Usuario();
            usuario2.setEmail("listarJunit2@email.com");
            usuario2.setSenha(passwordEncoder.encode("123456"));
            usuario2.setNome("Listar JUnit 2");
            usuario2.setUsername("listarJunit2");
            usuario2.setDtNascimento(LocalDate.of(2000, 1, 1));

            usuarioRepository.saveAll(Arrays.asList(usuario1, usuario2));

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("nome", "")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<UsuarioResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getEmail().equals(usuario1.getEmail()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getEmail().equals(usuario2.getEmail())))
            );
        }

        @Test
        void testListarUsuariosPorNome() throws Exception {
            final Usuario usuario1 = new Usuario();
            usuario1.setEmail("listarJunit1@email.com");
            usuario1.setSenha(passwordEncoder.encode("123456"));
            usuario1.setNome("Listar JUnit 1");
            usuario1.setUsername("listarJunit1");
            usuario1.setDtNascimento(LocalDate.of(2000, 1, 1));

            final Usuario usuario2 = new Usuario();
            usuario2.setEmail("listarJunit2@email.com");
            usuario2.setSenha(passwordEncoder.encode("123456"));
            usuario2.setNome("Listar JUnit 2");
            usuario2.setUsername("listarJunit2");
            usuario2.setDtNascimento(LocalDate.of(2000, 1, 1));

            usuarioRepository.saveAll(Arrays.asList(usuario1, usuario2));

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("nome", "listarJunit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<UsuarioResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getEmail().equals(usuario1.getEmail()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getEmail().equals(usuario2.getEmail()))),
                    () -> assertEquals(2, resultado.size())
            );
        }
    }

    @Nested
    class CriacaosuarioInconsistente {

        @Test
        void testCriarUsuarioFotoNula() throws Exception {
            final UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setEmail("criarJunit@email.com");
            dto.setSenha("123456");
            dto.setNome("Criar JUnit");
            dto.setUsername("criarJunit");
            dto.setDtNascimento(LocalDate.of(2000, 1, 1));

            final String responseJson = driver.perform(multipart(URI + "/criar")
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
        void testCriarUsuarioFotoVazia() throws Exception {
            final UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setEmail("criarJunit@email.com");
            dto.setSenha("123456");
            dto.setNome("Criar JUnit");
            dto.setUsername("criarJunit");
            dto.setDtNascimento(LocalDate.of(2000, 1, 1));

            final MockMultipartFile fotoVazia = new MockMultipartFile(
                    "foto",
                    "perfil.jpg",
                    "image/jpeg",
                    new byte[0] // foto vazia
            );

            final String responseJson = driver.perform(multipart(URI + "/criar")
                            .file(fotoVazia)
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
    }

    @Nested
    class ListagemUsuarioInconsistente {

        @Test
        void testListarUsuariosNomeInvalido() throws Exception {
            driver.perform(get(URI + "/listar")
                            .param("nome", "usuarioinexistente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        void testListarUsuariosNomeVazio() throws Exception {
            driver.perform(get(URI + "/listar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("nome", "")
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void testListarUsuariosNomeNulo() throws Exception {
            driver.perform(get(URI + "/listar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class VerificarPermissaoUsuario {

        @Test
        void testUsuarioAutenticado() throws Exception {
            final String responseText = driver.perform(
                            multipart(URI + "/atualizar/" + usuario.getId())
                                    .param("email", usuarioRequestDTO.getEmail())
                                    .param("senha", usuarioRequestDTO.getSenha())
                                    .param("nome", usuarioRequestDTO.getNome())
                                    .param("username", usuarioRequestDTO.getUsername())
                                    .param("dtNascimento", usuarioRequestDTO.getDtNascimento().toString())
                                    .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                    })
                                    .header("Authorization", "Bearer tokenInvalido")
                                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            assertEquals("Token JWT inválido ou expirado.", responseText);
        }


        @Test
        void testAtualizarUsuarioSemPermissao() throws Exception {
            final Usuario usuario1 = new Usuario();
            usuario1.setEmail("atualizarJunit@email.com");
            usuario1.setSenha("123456");
            usuario1.setNome("Atualizar JUnit");
            usuario1.setUsername("atualizarJunit");
            usuario1.setDtNascimento(LocalDate.of(2000, 1, 1));
            usuarioRepository.save(usuario1);

            final String responseText = driver.perform(
                            multipart(URI + "/atualizar/" + usuario1.getId())
                                    .param("email", usuario1.getEmail())
                                    .param("senha", usuario1.getSenha())
                                    .param("nome", usuario1.getNome())
                                    .param("username", usuario1.getUsername())
                                    .param("dtNascimento", usuario1.getDtNascimento().toString())
                                    .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                    })
                                    .header("Authorization", "Bearer " + token)
                                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            assertEquals("Usuário não tem permissão para realizar esta ação.", responseText);
        }


        @Test
        void testRemoverUsuarioSemPermissao() throws Exception {
            final Usuario usuario1 = new Usuario();
            usuario1.setEmail("atualizarJunit@email.com");
            usuario1.setSenha("123456");
            usuario1.setNome("Atualizar JUnit");
            usuario1.setUsername("atualizarJunit");
            usuario1.setDtNascimento(LocalDate.of(2000, 1, 1));
            usuarioRepository.save(usuario1);

            String responseText = driver.perform(delete(URI + "/remover/" + usuario1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals("Usuário não tem permissão para realizar esta ação.", responseText);
        }

    }

}
