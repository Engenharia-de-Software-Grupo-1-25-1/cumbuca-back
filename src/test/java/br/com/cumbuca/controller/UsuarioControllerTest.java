package br.com.cumbuca.controller;

import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
import br.com.cumbuca.model.Usuario;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsuarioControllerTest {
    final String URI = "/usuario";

    @Autowired
    MockMvc driver;

    @Autowired
    UsuarioRepository usuarioRepository;

    Usuario usuario;
    UsuarioRequestDTO usuarioRequestDTO;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setEmail("teste@email.com");
        usuarioRequestDTO.setSenha("123456");
        usuarioRequestDTO.setNome("Usuário Teste");
        usuarioRequestDTO.setUsername("teste");
        usuarioRequestDTO.setDtNascimento(LocalDate.of(2000, 1, 1));
        MockMultipartFile foto = new MockMultipartFile(
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
            String responseJson = driver.perform(post(URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

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
