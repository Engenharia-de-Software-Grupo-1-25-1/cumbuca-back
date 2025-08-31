package br.com.cumbuca.controller;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
class EstabelecimentoControllerTest {

    final String URI = "/estabelecimento";

    @Autowired
    MockMvc driver;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ObjectMapper objectMapper;

    ModelMapper modelMapper = new ModelMapper();
    EstabelecimentoRequestDTO estabelecimentoRequestDTO;
    Estabelecimento estabelecimento;

    @BeforeEach
    void setup() {
        estabelecimentoRequestDTO = new EstabelecimentoRequestDTO();
        estabelecimentoRequestDTO.setNome("Padaria Cumbuca");
        estabelecimentoRequestDTO.setCategoria("Padaria");
        estabelecimentoRequestDTO.setRua("Rua Principal");
        estabelecimentoRequestDTO.setNumero("123");
        estabelecimentoRequestDTO.setBairro("Centro");
        estabelecimentoRequestDTO.setCidade("Campina Grande");
        estabelecimentoRequestDTO.setEstado("PB");
        estabelecimentoRequestDTO.setCep("58400-000");
        estabelecimentoRequestDTO.setHorarios(Arrays.asList("08:00-12:00", "14:00-18:00"));

        estabelecimento = modelMapper.map(estabelecimentoRequestDTO, Estabelecimento.class);
        estabelecimentoRepository.save(estabelecimento);
    }

    @AfterEach
    void tearDown() {
        estabelecimentoRepository.deleteAll();
    }

    @Nested
    class FluxoBasico {

        @Test
        void testCriarEstabelecimento() throws Exception {
            EstabelecimentoRequestDTO dto = new EstabelecimentoRequestDTO();
            dto.setNome("Cafeteria Central");
            dto.setCategoria("Café");
            dto.setRua("Av. Brasil");
            dto.setNumero("456");
            dto.setBairro("Centro");
            dto.setCidade("Campina Grande");
            dto.setEstado("PB");
            dto.setCep("58400-000");
            dto.setHorarios(List.of("08:00-12:00", "14:00-18:00"));

            String responseJson = driver.perform(post(URI + "/criar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJson, EstabelecimentoResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(dto.getNome(), resultado.getNome()),
                    () -> assertEquals(dto.getCategoria(), resultado.getCategoria())
            );
        }

        @Test
        void testAtualizarEstabelecimento() throws Exception {
            EstabelecimentoRequestDTO dto = new EstabelecimentoRequestDTO();
            dto.setNome("Padaria Atualizada");
            dto.setCategoria("Padaria");
            dto.setRua("Rua Nova");
            dto.setNumero("999");
            dto.setBairro("Centro");
            dto.setCidade("Campina Grande");
            dto.setEstado("PB");
            dto.setCep("58400-000");
            dto.setHorarios(List.of("07:00-12:00", "13:00-19:00"));

            String responseJson = driver.perform(put(URI + "/atualizar/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJson, EstabelecimentoResponseDTO.class);

            assertAll(
                    () -> assertEquals(estabelecimento.getId(), resultado.getId()),
                    () -> assertEquals(dto.getNome(), resultado.getNome()),
                    () -> assertEquals(dto.getCategoria(), resultado.getCategoria())
            );
        }

        @Test
        void testRecuperarEstabelecimentoPorId() throws Exception {
            String responseJson = driver.perform(get(URI + "/recuperar/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJson, EstabelecimentoResponseDTO.class);

            assertAll(
                    () -> assertEquals(estabelecimento.getId(), resultado.getId()),
                    () -> assertEquals(estabelecimento.getNome(), resultado.getNome()),
                    () -> assertEquals(estabelecimento.getCategoria(), resultado.getCategoria())
            );
        }

        @Test
        void testRemoverEstabelecimento() throws Exception {
            driver.perform(delete(URI + "/remover/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertFalse(estabelecimentoRepository.findById(estabelecimento.getId()).isPresent());
        }

        @Test
        void testListarEstabelecimentos() throws Exception {
            Estabelecimento est1 = new Estabelecimento();
            est1.setNome("Restaurante 1");
            est1.setCategoria("Café");

            Estabelecimento est2 = new Estabelecimento();
            est2.setNome("Café 2");
            est2.setCategoria("Café");

            estabelecimentoRepository.saveAll(Arrays.asList(est1, est2));

            String responseJson = driver.perform(get(URI + "/listar")
                            .param("nome", "")
                            .param("categoria", "")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            List<EstabelecimentoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(e -> e.getNome().equals(est1.getNome()))),
                    () -> assertTrue(resultado.stream().anyMatch(e -> e.getNome().equals(est2.getNome())))
            );
        }
    }

    @Nested
    class CasosInconsistentes {

        @Test
        void testCriarEstabelecimentoNomeNulo() throws Exception {
            EstabelecimentoRequestDTO dto = new EstabelecimentoRequestDTO();
            dto.setCategoria("Café");

            driver.perform(post(URI + "/criar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testCriarEstabelecimentoCategoriaNula() throws Exception {
            EstabelecimentoRequestDTO dto = new EstabelecimentoRequestDTO();
            dto.setNome("Cafeteria X");

            driver.perform(post(URI + "/criar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
