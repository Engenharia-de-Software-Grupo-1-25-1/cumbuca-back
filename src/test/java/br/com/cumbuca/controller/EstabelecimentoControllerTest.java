package br.com.cumbuca.controller;

import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.EstabelecimentoViewRepository;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EstabelecimentoControllerTest {
    final String URI = "/estabelecimento";

    @Autowired
    MockMvc driver;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EstabelecimentoViewRepository estabelecimentoViewRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    ModelMapper modelMapper = new ModelMapper();
    UsuarioRequestDTO usuarioRequestDTO;
    Usuario usuario;
    EstabelecimentoRequestDTO estabelecimentoRequestDTO;
    Estabelecimento estabelecimento;
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

        estabelecimentoRequestDTO = new EstabelecimentoRequestDTO();
        estabelecimentoRequestDTO.setId(1L);
        estabelecimentoRequestDTO.setNome("Teste JUnit");
        estabelecimentoRequestDTO.setCategoria("Restaurante");
        estabelecimentoRequestDTO.setRua("Rua dos Testes");
        estabelecimentoRequestDTO.setNumero("87");
        estabelecimentoRequestDTO.setBairro("Vila dos Testes");
        estabelecimentoRequestDTO.setEstado("PB");
        estabelecimentoRequestDTO.setCep("00000-000");

        estabelecimento = modelMapper.map(estabelecimentoRequestDTO, Estabelecimento.class);
        estabelecimentoRepository.save(estabelecimento);
        estabelecimentoRepository.flush();
    }

    @AfterEach
    void tearDown() {
        avaliacaoRepository.deleteAll();
        usuarioRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        estabelecimentoViewRepository.deleteAll();
    }

    @Nested
    class EstabelecimentoFluxoBasicoApiRest {

         @Test
         void testRecuperarEstabelecimento() throws Exception {
             final String responseJson = driver.perform(get(URI + "/recuperar/" + estabelecimento.getId())
                             .contentType(MediaType.APPLICATION_JSON)
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk())
                     .andReturn()
                     .getResponse()
                     .getContentAsString(StandardCharsets.UTF_8);

             final EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJson, EstabelecimentoResponseDTO.class);

             assertAll(
                     () -> assertNotNull(resultado.getId()),
                     () -> assertEquals(estabelecimento.getId(), resultado.getId()),
                     () -> assertEquals(estabelecimento.getNome(), resultado.getNome()),
                     () -> assertEquals(estabelecimento.getCategoria(), resultado.getCategoria()),
                     () -> assertEquals(estabelecimento.getRua(), resultado.getRua()),
                     () -> assertEquals(estabelecimento.getNumero(), resultado.getNumero()),
                     () -> assertEquals(estabelecimento.getBairro(), resultado.getBairro()),
                     () -> assertEquals(estabelecimento.getEstado(), resultado.getEstado()),
                     () -> assertEquals(estabelecimento.getCep(), resultado.getCep())
             );
         }

        @Test
        void testFavoritarEstabelecimento() throws Exception {
            final String responseJson = driver.perform(post(URI + "/favoritar/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJson, EstabelecimentoResponseDTO.class);

            assertTrue(resultado.getIsFavoritado());
        }

        @Test
        void testDesfavoritarEstabelecimento() throws Exception {
            driver.perform(post(URI + "/favoritar/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk());

            final String responseJson = driver.perform(post(URI + "/favoritar/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJson, EstabelecimentoResponseDTO.class);

            assertFalse(resultado.getIsFavoritado());
        }

         @Test
         void testListarEstabelecimentos() throws Exception {
             final Estabelecimento e2 = new Estabelecimento();
             e2.setId(2L);
             e2.setNome("Teste JUnit");
             e2.setCategoria("Pizzaria");
             e2.setRua("Rua Central");
             e2.setNumero("101");
             e2.setBairro("Centro");
             e2.setEstado("PB");
             e2.setCep("58000-000");

             final Estabelecimento e3 = new Estabelecimento();
             e3.setId(3L);
             e3.setNome("Café Gourmet");
             e3.setCategoria("Cafeteria");
             e3.setRua("Av. Principal");
             e3.setNumero("200");
             e3.setBairro("Jardins");
             e3.setEstado("SP");
             e3.setCep("01000-000");

             estabelecimentoRepository.saveAll(Arrays.asList(e2, e3));
             estabelecimentoRepository.flush();

             final String responseJson = driver.perform(get(URI + "/listar")
                             .contentType(MediaType.APPLICATION_JSON)
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk())
                     .andReturn()
                     .getResponse()
                     .getContentAsString(StandardCharsets.UTF_8);


             final List<EstabelecimentoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
             });

             assertAll(
                     () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(estabelecimento.getId()))),
                     () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e2.getId()))),
                     () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e3.getId()))),
                     () -> assertEquals(3, resultado.size())
             );
         }

        @Test
        void testListarEstabelecimentosPorNome() throws Exception {
            final Estabelecimento e2 = new Estabelecimento();
            e2.setId(2L);
            e2.setNome("Teste JUnit");;
            e2.setCategoria("Pizzaria");
            e2.setRua("Rua Central");
            e2.setNumero("101");
            e2.setBairro("Centro");
            e2.setEstado("PB");
            e2.setCep("58000-000");

            final Estabelecimento e3 = new Estabelecimento();
            e3.setId(3L);
            e3.setNome("Café Gourmet");
            e3.setCategoria("Cafeteria");
            e3.setRua("Av. Principal");
            e3.setNumero("200");
            e3.setBairro("Jardins");
            e3.setEstado("SP");
            e3.setCep("01000-000");

            estabelecimentoRepository.saveAll(Arrays.asList(e2, e3));
            estabelecimentoRepository.flush();

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("nome", e2.getNome())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<EstabelecimentoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(estabelecimento.getId()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e2.getId()))),
                    () -> assertFalse(resultado.stream().anyMatch(u -> u.getId().equals(e3.getId()))),
                    () -> assertEquals(2, resultado.size())
            );
        }

        @Test
        void testListarEstabelecimentosPorCategoria() throws Exception {
            final Estabelecimento e2 = new Estabelecimento();
            e2.setId(2L);
            e2.setNome("Starbucks");
            e2.setCategoria("Cafeteria");
            e2.setRua("Rua Central");
            e2.setNumero("101");
            e2.setBairro("Centro");
            e2.setEstado("PB");
            e2.setCep("58000-000");

            final Estabelecimento e3 = new Estabelecimento();
            e3.setId(3L);
            e3.setNome("Café Gourmet");
            e3.setCategoria("Cafeteria");
            e3.setRua("Av. Principal");
            e3.setNumero("200");
            e3.setBairro("Jardins");
            e3.setEstado("SP");
            e3.setCep("01000-000");

            estabelecimentoRepository.saveAll(Arrays.asList(e2, e3));
            estabelecimentoRepository.flush();

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("categoria", e2.getCategoria())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<EstabelecimentoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertFalse(resultado.stream().anyMatch(u -> u.getId().equals(estabelecimento.getId()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e2.getId()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e3.getId()))),
                    () -> assertEquals(2, resultado.size())
            );
        }

        @Test
        void testListarEstabelecimentosPorLocalizacao() throws Exception {
            final Estabelecimento e2 = new Estabelecimento();
            e2.setId(2L);
            e2.setNome("Starbucks");
            e2.setCategoria("Cafeteria");
            e2.setRua("Rua Central");
            e2.setNumero("101");
            e2.setBairro("Centro");
            e2.setEstado("PB");
            e2.setCidade("João Pessoa");
            e2.setCep("58000-000");

            final Estabelecimento e3 = new Estabelecimento();
            e3.setId(3L);
            e3.setNome("Café Gourmet");
            e3.setCategoria("Cafeteria Gourmet");
            e3.setRua("Rua Central");
            e3.setNumero("101");
            e3.setBairro("Centro");
            e3.setEstado("PB");
            e3.setCidade("João Pessoa");
            e3.setCep("58000-000");

            estabelecimentoRepository.saveAll(Arrays.asList(e2, e3));
            estabelecimentoRepository.flush();

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("localizacao", e2.getCep())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<EstabelecimentoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertFalse(resultado.stream().anyMatch(u -> u.getId().equals(estabelecimento.getId()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e2.getId()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e3.getId()))),
                    () -> assertEquals(2, resultado.size())
            );
        }

        @Test
        void testListarEstabelecimentosFavoritos() throws Exception {
            final Estabelecimento e2 = new Estabelecimento();
            e2.setId(2L);
            e2.setNome("Starbucks");
            e2.setCategoria("Cafeteria");
            e2.setRua("Rua Central");
            e2.setNumero("101");
            e2.setBairro("Centro");
            e2.setEstado("PB");
            e2.setCep("58000-000");

            final Estabelecimento e3 = new Estabelecimento();
            e3.setId(3L);
            e3.setNome("Café Gourmet");
            e3.setCategoria("Cafeteria");
            e3.setRua("Av. Principal");
            e3.setNumero("200");
            e3.setBairro("Jardins");
            e3.setEstado("SP");
            e3.setCep("01000-000");

            estabelecimentoRepository.saveAll(Arrays.asList(e2, e3));
            estabelecimentoRepository.flush();

            driver.perform(post(URI + "/favoritar/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk());

            driver.perform(post(URI + "/favoritar/" + e2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk());

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("isFavoritado", "true")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<EstabelecimentoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(estabelecimento.getId()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e2.getId()))),
                    () -> assertFalse(resultado.stream().anyMatch(u -> u.getId().equals(e3.getId()))),
                    () -> assertEquals(2, resultado.size())
            );
        }

        @Test
        void testListarEstabelecimentosNaoFavoritos() throws Exception {
            final Estabelecimento e2 = new Estabelecimento();
            e2.setId(2L);
            e2.setNome("Starbucks");
            e2.setCategoria("Cafeteria");
            e2.setRua("Rua Central");
            e2.setNumero("101");
            e2.setBairro("Centro");
            e2.setEstado("PB");
            e2.setCep("58000-000");

            final Estabelecimento e3 = new Estabelecimento();
            e3.setId(3L);
            e3.setNome("Café Gourmet");
            e3.setCategoria("Cafeteria");
            e3.setRua("Av. Principal");
            e3.setNumero("200");
            e3.setBairro("Jardins");
            e3.setEstado("SP");
            e3.setCep("01000-000");

            estabelecimentoRepository.saveAll(Arrays.asList(e2, e3));
            estabelecimentoRepository.flush();

            driver.perform(post(URI + "/favoritar/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk());

            driver.perform(post(URI + "/favoritar/" + e2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk());

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("isFavoritado", "false")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<EstabelecimentoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertFalse(resultado.stream().anyMatch(u -> u.getId().equals(estabelecimento.getId()))),
                    () -> assertFalse(resultado.stream().anyMatch(u -> u.getId().equals(e2.getId()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e3.getId()))),
                    () -> assertEquals(1, resultado.size())
            );
        }

        @Test
        void testListarEstabelecimentosPorNotaGeral() throws Exception {
            final Estabelecimento e2 = new Estabelecimento();
            e2.setId(2L);
            e2.setNome("Starbucks");
            e2.setCategoria("Cafeteria");
            e2.setRua("Rua Central");
            e2.setNumero("101");
            e2.setBairro("Centro");
            e2.setEstado("PB");
            e2.setCep("58000-000");

            final Estabelecimento e3 = new Estabelecimento();
            e3.setId(3L);
            e3.setNome("Café Gourmet");
            e3.setCategoria("Cafeteria");
            e3.setRua("Av. Principal");
            e3.setNumero("200");
            e3.setBairro("Jardins");
            e3.setEstado("SP");
            e3.setCep("01000-000");

            estabelecimentoRepository.saveAll(Arrays.asList(e2, e3));
            estabelecimentoRepository.flush();

            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setUsuario(usuario);
            avaliacao.setEstabelecimento(estabelecimento);
            avaliacao.setDescricao("teste");
            avaliacao.setNotaGeral(4);
            avaliacao.setItemConsumido("teste");
            avaliacaoRepository.save(avaliacao);

            Avaliacao avaliacao2 = new Avaliacao();
            avaliacao2.setUsuario(usuario);
            avaliacao2.setEstabelecimento(e2);
            avaliacao2.setDescricao("teste");
            avaliacao2.setNotaGeral(4);
            avaliacao2.setItemConsumido("teste");
            avaliacaoRepository.save(avaliacao2);

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("notaGeral", "4.0")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<EstabelecimentoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(estabelecimento.getId()))),
                    () -> assertTrue(resultado.stream().anyMatch(u -> u.getId().equals(e2.getId()))),
                    () -> assertFalse(resultado.stream().anyMatch(u -> u.getId().equals(e3.getId())))
            );
        }

     @Nested
     class ListagemEstabelecimentoInconsistente {

         @Test
         void testListarEstabelecimentosNomeInvalido() throws Exception {
             driver.perform(get(URI + "/listar")
                             .param("nome", "estabelecimentoinexistente")
                             .contentType(MediaType.APPLICATION_JSON)
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk())
                     .andExpect(jsonPath("$").isEmpty());
         }

         @Test
         void testListarEstabelecimentosNomeVazio() throws Exception {
             driver.perform(get(URI + "/listar")
                             .contentType(MediaType.APPLICATION_JSON)
                             .param("nome", "")
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk());
         }

         @Test
         void testListarEstabelecimentosCategoriaInvalida() throws Exception {
             driver.perform(get(URI + "/listar")
                             .param("categoria", "categoriainexistente")
                             .contentType(MediaType.APPLICATION_JSON)
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk())
                     .andExpect(jsonPath("$").isEmpty());
         }

         @Test
         void testListarEstabelecimentosCategoriaVazia() throws Exception {
             driver.perform(get(URI + "/listar")
                             .contentType(MediaType.APPLICATION_JSON)
                             .param("categoria", "")
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk());
         }

         @Test
         void testListarEstabelecimentosLocalizacaoInvalida() throws Exception {
             driver.perform(get(URI + "/listar")
                             .param("localizacao", "localizacaoinexistente")
                             .contentType(MediaType.APPLICATION_JSON)
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk())
                     .andExpect(jsonPath("$").isEmpty());
         }

         @Test
         void testListarEstabelecimentosLocalizacaoVazia() throws Exception {
             driver.perform(get(URI + "/listar")
                             .contentType(MediaType.APPLICATION_JSON)
                             .param("localizacao", "")
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk());
         }

         @Test
         void testListarEstabelecimentosFavoritoVazio() throws Exception {
             driver.perform(get(URI + "/listar")
                             .contentType(MediaType.APPLICATION_JSON)
                             .param("isFavorito", "")
                             .header("Authorization", "Bearer " + token))
                     .andDo(print())
                     .andExpect(status().isOk());
         }

     }

        @Nested
        class RecuperarEstabelecimentoInconsistente {

            @Test
            void testRecuperarEstabelecimentoInexistente() throws Exception {
                driver.perform(get(URI + "/recuperar/" + 555L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token))
                        .andDo(print())
                        .andExpect(status().isNotFound());
            }

            @Test
            void testRecuperarEstabelecimentoIdNulo() throws Exception {
                driver.perform(get(URI + "/recuperar/" + null)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token))
                        .andDo(print())
                        .andExpect(status().isInternalServerError());
            }

        }

    }

}
