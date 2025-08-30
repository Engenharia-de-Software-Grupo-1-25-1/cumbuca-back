package br.com.cumbuca.controller;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
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
    ObjectMapper objectMapper;

    UsuarioRequestDTO usuarioRequestDTO;
    MockMultipartFile foto;

    @BeforeEach
    void setup() {
        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setEmail("testejunit@email.com");
        usuarioRequestDTO.setSenha("123456");
        usuarioRequestDTO.setNome("Teste JUnit");
        usuarioRequestDTO.setUsername("testejunit");
        usuarioRequestDTO.setDtNascimento(LocalDate.of(2000, 1, 1));

        foto = new MockMultipartFile(
                "foto",
                "perfil.jpg",
                "image/jpeg",
                "conteudo da foto".getBytes());
        usuarioRequestDTO.setFoto(foto);
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Nested
    class UsuarioFluxoBasicoApiRest {

        @Test
        void testCriarUsuario() throws Exception {
            String responseJson = driver.perform(multipart(URI + "/criar")
                            .file(foto)
                            .param("email", usuarioRequestDTO.getEmail())
                            .param("senha", usuarioRequestDTO.getSenha())
                            .param("nome", usuarioRequestDTO.getNome())
                            .param("username", usuarioRequestDTO.getUsername())
                            .param("dtNascimento", usuarioRequestDTO.getDtNascimento().toString())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            UsuarioResponseDTO resultado = objectMapper.readValue(responseJson, UsuarioResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(usuarioRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(usuarioRequestDTO.getUsername(), resultado.getUsername()),
                    () -> assertEquals(usuarioRequestDTO.getEmail(), resultado.getEmail()),
                    () -> assertEquals(usuarioRequestDTO.getDtNascimento(), resultado.getDtNascimento())
            );
        }
    }
}
