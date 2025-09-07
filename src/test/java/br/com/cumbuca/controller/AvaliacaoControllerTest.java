package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.TagRepository;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.autenticacao.TokenService;
import br.com.cumbuca.utils.ImageCompressor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AvaliacaoControllerTest {
    static final String URI = "/avaliacao";

    @Autowired
    MockMvc driver;

    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    UsuarioRequestDTO usuarioRequestDTO;
    Usuario usuario;
    EstabelecimentoRequestDTO estabelecimentoRequestDTO;
    Estabelecimento estabelecimento;
    AvaliacaoRequestDTO avaliacaoRequestDTO;
    Avaliacao avaliacao;
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
        tagRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        usuarioRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    @Nested
    class AvaliacaoFluxoBasicoApiRest {

        @Test
        void testCriarAvaliacao() throws Exception {
            final AvaliacaoRequestDTO avaliacaoRequest = new AvaliacaoRequestDTO();
            avaliacaoRequest.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoRequest.setItemConsumido("Pizza Margherita");
            avaliacaoRequest.setDescricao("Excelente pizza, massa crocante e ingredientes frescos!");
            avaliacaoRequest.setPreco(new BigDecimal("35.90"));
            avaliacaoRequest.setNotaGeral(5);
            avaliacaoRequest.setNotaComida(5);
            avaliacaoRequest.setNotaAtendimento(4);
            avaliacaoRequest.setNotaAmbiente(4);
            avaliacaoRequest.setTags(Arrays.asList("pizza", "deliciosa", "recomendo"));

            final MockMultipartFile foto = new MockMultipartFile("fotos", "pizza.jpg", "image/jpeg",
                    "conteudo da foto".getBytes());

            final String responseJson = driver.perform(multipart(URI + "/criar")
                            .file(foto)
                            .param("estabelecimento.id", estabelecimentoRequestDTO.getId().toString())
                            .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                            .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                            .param("itemConsumido", avaliacaoRequest.getItemConsumido())
                            .param("descricao", avaliacaoRequest.getDescricao())
                            .param("preco", avaliacaoRequest.getPreco().toString())
                            .param("notaGeral", avaliacaoRequest.getNotaGeral().toString())
                            .param("notaComida", avaliacaoRequest.getNotaComida().toString())
                            .param("notaAtendimento", avaliacaoRequest.getNotaAtendimento().toString())
                            .param("notaAmbiente", avaliacaoRequest.getNotaAmbiente().toString())
                            .param("tags", "pizza", "deliciosa", "recomendo")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final AvaliacaoResponseDTO resultado = objectMapper.readValue(responseJson, AvaliacaoResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(avaliacaoRequest.getItemConsumido(), resultado.getItemConsumido()),
                    () -> assertEquals(avaliacaoRequest.getDescricao(), resultado.getDescricao()),
                    () -> assertEquals(avaliacaoRequest.getPreco(), resultado.getPreco()),
                    () -> assertEquals(avaliacaoRequest.getNotaGeral(), resultado.getNotaGeral()),
                    () -> assertEquals(avaliacaoRequest.getNotaComida(), resultado.getNotaComida()),
                    () -> assertEquals(avaliacaoRequest.getNotaAtendimento(), resultado.getNotaAtendimento()),
                    () -> assertEquals(avaliacaoRequest.getNotaAmbiente(), resultado.getNotaAmbiente()),
                    () -> assertEquals(usuario.getId(), resultado.getUsuario().getId()),
                    () -> assertEquals(estabelecimento.getId(), resultado.getEstabelecimento().getId()),
                    () -> assertNotNull(resultado.getData()));
        }

        @Test
        void testAtualizarAvaliacao() throws Exception {
            final AvaliacaoRequestDTO avaliacaoAtualizadaDTO = new AvaliacaoRequestDTO();
            avaliacaoAtualizadaDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoAtualizadaDTO.setItemConsumido("Hambúrguer Artesanal");
            avaliacaoAtualizadaDTO.setDescricao("Hambúrguer delicioso com ingredientes de qualidade!");
            avaliacaoAtualizadaDTO.setPreco(new BigDecimal("42.50"));
            avaliacaoAtualizadaDTO.setTags(Arrays.asList("hambúrguer", "artesanal", "saboroso"));

            final MockMultipartFile foto = new MockMultipartFile("fotos", "hamburguer.jpg", "image/jpeg",
                    "conteudo da foto atualizada".getBytes());

            final String responseJson = driver.perform(multipart(URI + "/atualizar/" + avaliacao.getId())
                            .file(foto)
                            .param("estabelecimento.id", estabelecimentoRequestDTO.getId().toString())
                            .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                            .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                            .param("itemConsumido", avaliacaoAtualizadaDTO.getItemConsumido())
                            .param("descricao", avaliacaoAtualizadaDTO.getDescricao())
                            .param("preco", avaliacaoAtualizadaDTO.getPreco().toString())
                            .param("tags", "hambúrguer", "artesanal", "saboroso")
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final AvaliacaoResponseDTO resultado = objectMapper.readValue(responseJson, AvaliacaoResponseDTO.class);

            assertAll(
                    () -> assertEquals(avaliacao.getId(), resultado.getId()),
                    () -> assertEquals(avaliacaoAtualizadaDTO.getItemConsumido(), resultado.getItemConsumido()),
                    () -> assertEquals(avaliacaoAtualizadaDTO.getDescricao(), resultado.getDescricao()),
                    () -> assertEquals(avaliacaoAtualizadaDTO.getPreco(), resultado.getPreco()),
                    () -> assertEquals(usuario.getId(), resultado.getUsuario().getId()),
                    () -> assertEquals(estabelecimento.getId(), resultado.getEstabelecimento().getId()),
                    () -> assertNotNull(resultado.getData()));
        }

        @Test
        void testRemoverAvaliacao() throws Exception {
            driver.perform(delete(URI + "/remover/" + avaliacao.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isNoContent());
            assertFalse(avaliacaoRepository.findById(avaliacao.getId()).isPresent());
        }

        @Test
        void testRecuperarAvaliacaoPorId() throws Exception {
            final String responseJson = driver.perform(get(URI + "/recuperar/" +
                            avaliacao.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final AvaliacaoResponseDTO resultado = objectMapper.readValue(responseJson,
                    AvaliacaoResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(avaliacao.getId(), resultado.getId()),
                    () -> assertEquals(avaliacao.getDescricao(), resultado.getDescricao()),
                    () -> assertEquals(avaliacao.getPreco(), resultado.getPreco()),
                    () -> assertEquals(avaliacao.getNotaComida(), resultado.getNotaComida()));
        }

        @Test
        void testListarAvaliacoes() throws Exception {
            final Avaliacao avaliacao1 = new Avaliacao();
            avaliacao1.setItemConsumido("Pizza Calabresa");
            avaliacao1.setDescricao("Pizza muito saborosa");
            avaliacao1.setPreco(new BigDecimal("30.00"));
            avaliacao1.setEstabelecimento(estabelecimento);
            avaliacao1.setUsuario(usuario);

            final Avaliacao avaliacao2 = new Avaliacao();
            avaliacao2.setItemConsumido("Hambúrguer");
            avaliacao2.setDescricao("Hambúrguer delicioso");
            avaliacao2.setPreco(new BigDecimal("35.00"));
            avaliacao2.setEstabelecimento(estabelecimento);
            avaliacao2.setUsuario(usuario);

            avaliacaoRepository.saveAll(Arrays.asList(avaliacao1, avaliacao2));

            final String responseJson = driver.perform(get(URI + "/listar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(
                            resultado.stream().anyMatch(a -> a.getDescricao().equals(avaliacao1.getDescricao()))),
                    () -> assertTrue(
                            resultado.stream().anyMatch(a -> a.getDescricao().equals(avaliacao2.getDescricao()))));
        }
    }


    @Nested
    class VerificarPermissaoAvaliacao {
        @Test
        void testAtualizarAvaliacaoSemPermissao() throws Exception {
            final UsuarioRequestDTO outroUsuarioDTO = new UsuarioRequestDTO();
            outroUsuarioDTO.setEmail("outroUsuario@email.com");
            outroUsuarioDTO.setSenha("123456");
            outroUsuarioDTO.setNome("Outro Usuario");
            outroUsuarioDTO.setUsername("outroUsuario");
            outroUsuarioDTO.setDtNascimento(LocalDate.of(2000, 1, 1));

            final Usuario outroUsuario = modelMapper.map(outroUsuarioDTO, Usuario.class);
            outroUsuario.setSenha(passwordEncoder.encode(outroUsuarioDTO.getSenha()));
            usuarioRepository.save(outroUsuario);

            final Avaliacao avaliacaoOutroUsuario = new Avaliacao();
            avaliacaoOutroUsuario.setItemConsumido("Pizza Margherita");
            avaliacaoOutroUsuario.setDescricao("Pizza muito saborosa!");
            avaliacaoOutroUsuario.setPreco(new BigDecimal("28.00"));
            avaliacaoOutroUsuario.setEstabelecimento(estabelecimento);
            avaliacaoOutroUsuario.setUsuario(outroUsuario);
            avaliacaoRepository.save(avaliacaoOutroUsuario);

            final AvaliacaoRequestDTO avaliacaoAtualizadaDTO = new AvaliacaoRequestDTO();
            avaliacaoAtualizadaDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoAtualizadaDTO.setItemConsumido("Pizza Atualizada");
            avaliacaoAtualizadaDTO.setDescricao("Tentando atualizar avaliação de outro usuário");
            avaliacaoAtualizadaDTO.setPreco(new BigDecimal("35.00"));

            final String responseText = driver.perform(multipart(URI + "/atualizar/" + avaliacaoOutroUsuario.getId())
                            .param("estabelecimento.id", estabelecimentoRequestDTO.getId().toString())
                            .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                            .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                            .param("itemConsumido", avaliacaoAtualizadaDTO.getItemConsumido())
                            .param("descricao", avaliacaoAtualizadaDTO.getDescricao())
                            .param("preco", avaliacaoAtualizadaDTO.getPreco().toString())
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA)
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
        void testRemoverAvaliacaoSemPermissao() throws Exception {
            final UsuarioRequestDTO outroUsuarioDTO = new UsuarioRequestDTO();
            outroUsuarioDTO.setEmail("outroUsuario2@email.com");
            outroUsuarioDTO.setSenha("123456");
            outroUsuarioDTO.setNome("Outro Usuario 2");
            outroUsuarioDTO.setUsername("outroUsuario2");
            outroUsuarioDTO.setDtNascimento(LocalDate.of(2000, 1, 1));

            final Usuario outroUsuario = modelMapper.map(outroUsuarioDTO, Usuario.class);
            outroUsuario.setSenha(passwordEncoder.encode(outroUsuarioDTO.getSenha()));
            usuarioRepository.save(outroUsuario);

            final Avaliacao avaliacaoOutroUsuario = new Avaliacao();
            avaliacaoOutroUsuario.setItemConsumido("Lasanha");
            avaliacaoOutroUsuario.setDescricao("Lasanha deliciosa!");
            avaliacaoOutroUsuario.setPreco(new BigDecimal("32.00"));
            avaliacaoOutroUsuario.setEstabelecimento(estabelecimento);
            avaliacaoOutroUsuario.setUsuario(outroUsuario);
            avaliacaoRepository.save(avaliacaoOutroUsuario);

            final String responseText = driver.perform(delete(URI + "/remover/" + avaliacaoOutroUsuario.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals("Usuário não tem permissão para realizar esta ação.", responseText);
        }
    }


    @Nested
    class FiltrosAvaliacao {
        @Test
        void testFiltrarAvaliacoesPorPrecoMinimo() throws Exception {
            final Avaliacao avaliacaoBarata = new Avaliacao();
            avaliacaoBarata.setItemConsumido("Lanche Simples");
            avaliacaoBarata.setDescricao("Lanche barato e gostoso");
            avaliacaoBarata.setPreco(new BigDecimal("15.00"));
            avaliacaoBarata.setEstabelecimento(estabelecimento);
            avaliacaoBarata.setUsuario(usuario);

            final Avaliacao avaliacaoCara = new Avaliacao();
            avaliacaoCara.setItemConsumido("Prato Executivo");
            avaliacaoCara.setDescricao("Prato mais caro mas muito bom");
            avaliacaoCara.setPreco(new BigDecimal("45.00"));
            avaliacaoCara.setEstabelecimento(estabelecimento);
            avaliacaoCara.setUsuario(usuario);

            avaliacaoRepository.saveAll(Arrays.asList(avaliacaoBarata, avaliacaoCara));

            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("precoMinimo", "30.00")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(
                            resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("45.00")) == 0)),
                    () -> assertFalse(
                            resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("15.00")) == 0)));
        }
    }

    @Test
    void testFiltrarAvaliacoesPorPrecoMaximo() throws Exception {
        final Avaliacao avaliacaoBarata = new Avaliacao();
        avaliacaoBarata.setItemConsumido("Lanche Simples");
        avaliacaoBarata.setDescricao("Lanche barato e gostoso");
        avaliacaoBarata.setPreco(new BigDecimal("15.00"));
        avaliacaoBarata.setEstabelecimento(estabelecimento);
        avaliacaoBarata.setUsuario(usuario);

        final Avaliacao avaliacaoCara = new Avaliacao();
        avaliacaoCara.setItemConsumido("Prato Executivo");
        avaliacaoCara.setDescricao("Prato mais caro mas muito bom");
        avaliacaoCara.setPreco(new BigDecimal("45.00"));
        ;
        avaliacaoCara.setEstabelecimento(estabelecimento);
        avaliacaoCara.setUsuario(usuario);

        avaliacaoRepository.saveAll(Arrays.asList(avaliacaoBarata, avaliacaoCara));

        final String responseJson = driver.perform(get(URI + "/listar")
                        .param("precoMaximo", "20.00")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertAll(
                () -> assertTrue(
                        resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("15.00")) == 0)),
                () -> assertFalse(
                        resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("45.00")) == 0)));
    }

    @Test
    void testFiltrarAvaliacoesPorTags() throws Exception {
        final AvaliacaoRequestDTO avaliacaoComTagDTO = new AvaliacaoRequestDTO();
        avaliacaoComTagDTO.setEstabelecimento(estabelecimentoRequestDTO);
        avaliacaoComTagDTO.setItemConsumido("Pizza Margherita");
        avaliacaoComTagDTO.setDescricao("Pizza deliciosa!");
        avaliacaoComTagDTO.setPreco(new BigDecimal("28.00"));
        avaliacaoComTagDTO.setTags(Arrays.asList("pizza", "italiana", "queijo"));

        final MockMultipartFile foto1 = new MockMultipartFile("fotos", "pizza.jpg", "image/jpeg",
                "conteudo da foto".getBytes());

        driver.perform(multipart(URI + "/criar")
                        .file(foto1)
                        .param("estabelecimento.id", estabelecimentoRequestDTO.getId().toString())
                        .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                        .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                        .param("itemConsumido", avaliacaoComTagDTO.getItemConsumido())
                        .param("descricao", avaliacaoComTagDTO.getDescricao())
                        .param("preco", avaliacaoComTagDTO.getPreco().toString())
                        .param("tags", "pizza", "italiana", "queijo")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + token)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final AvaliacaoRequestDTO avaliacaoSemTagDTO = new AvaliacaoRequestDTO();
        avaliacaoSemTagDTO.setEstabelecimento(estabelecimentoRequestDTO);
        avaliacaoSemTagDTO.setItemConsumido("Hambúrguer");
        avaliacaoSemTagDTO.setDescricao("Hambúrguer saboroso!");
        avaliacaoSemTagDTO.setPreco(new BigDecimal("32.00"));

        final MockMultipartFile foto2 = new MockMultipartFile("fotos", "hamburguer.jpg", "image/jpeg",
                "conteudo da foto".getBytes());

        driver.perform(multipart(URI + "/criar")
                        .file(foto2)
                        .param("estabelecimento.id", estabelecimentoRequestDTO.getId().toString())
                        .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                        .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                        .param("itemConsumido", avaliacaoSemTagDTO.getItemConsumido())
                        .param("descricao", avaliacaoSemTagDTO.getDescricao())
                        .param("preco", avaliacaoSemTagDTO.getPreco().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + token)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isCreated());

        final String responseJsonFiltrado = driver.perform(get(URI + "/listar")
                        .param("tags", "pizza")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final List<AvaliacaoResponseDTO> resultadoFiltrado = objectMapper.readValue(responseJsonFiltrado,
                new TypeReference<>() {
                });

        assertAll(
                () -> assertTrue(
                        resultadoFiltrado.stream().anyMatch(a -> a.getItemConsumido().equals("Pizza Margherita"))),
                () -> assertFalse(
                        resultadoFiltrado.stream().anyMatch(a -> a.getItemConsumido().equals("Hambúrguer"))));
    }

    @Test
    void testFiltrarAvaliacoesPorNomeUsuario() throws Exception {
        final UsuarioRequestDTO outroUsuarioDTO = new UsuarioRequestDTO();
        outroUsuarioDTO.setEmail("outroUsuario@email.com");
        outroUsuarioDTO.setSenha("123456");
        outroUsuarioDTO.setNome("João Silva");
        outroUsuarioDTO.setUsername("joaosilva");
        outroUsuarioDTO.setDtNascimento(LocalDate.of(1995, 5, 15));

        final Usuario outroUsuario = modelMapper.map(outroUsuarioDTO, Usuario.class);
        outroUsuario.setSenha(passwordEncoder.encode(outroUsuarioDTO.getSenha()));
        usuarioRepository.save(outroUsuario);

        final Avaliacao avaliacaoOutroUsuario = new Avaliacao();
        avaliacaoOutroUsuario.setItemConsumido("Feijoada");
        avaliacaoOutroUsuario.setDescricao("Feijoada deliciosa!");
        avaliacaoOutroUsuario.setPreco(new BigDecimal("35.00"));
        avaliacaoOutroUsuario.setEstabelecimento(estabelecimento);
        avaliacaoOutroUsuario.setUsuario(outroUsuario);
        avaliacaoRepository.save(avaliacaoOutroUsuario);

        final String responseJson = driver.perform(get(URI + "/listar")
                        .param("usuario", "João")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertAll(
                () -> assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("Feijoada"))),
                () -> assertFalse(
                        resultado.stream().anyMatch(a -> a.getUsuario().getNome().equals("Teste JUnit"))));
    }


    @Test
    void testFiltrarAvaliacoesPorNomeEstabelecimento() throws Exception {
        final EstabelecimentoRequestDTO outroEstabelecimentoDTO = new EstabelecimentoRequestDTO();
        outroEstabelecimentoDTO.setId(2L);
        outroEstabelecimentoDTO.setNome("Pizzaria Bella Vista");
        outroEstabelecimentoDTO.setCategoria("Pizzaria");

        final Estabelecimento outroEstabelecimento = modelMapper.map(outroEstabelecimentoDTO,
                Estabelecimento.class);
        estabelecimentoRepository.save(outroEstabelecimento);

        final Avaliacao avaliacaoOutroEstabelecimento = new Avaliacao();
        avaliacaoOutroEstabelecimento.setItemConsumido("Pizza Napoletana");
        avaliacaoOutroEstabelecimento.setDescricao("Pizza autêntica italiana!");
        avaliacaoOutroEstabelecimento.setPreco(new BigDecimal("42.00"));
        avaliacaoOutroEstabelecimento.setEstabelecimento(outroEstabelecimento);
        avaliacaoOutroEstabelecimento.setUsuario(usuario);
        avaliacaoRepository.save(avaliacaoOutroEstabelecimento);

        final String responseJson = driver.perform(get(URI + "/listar")
                        .param("estabelecimento", "Bella")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertAll(
                () -> assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("Pizza Napoletana"))),
                () -> assertTrue(
                        resultado.stream().allMatch(a -> a.getEstabelecimento().getNome().contains("Bella"))));
    }


    @Nested
    class TestesFluxoListarCompleto {

        private Usuario outroUsuario;
        private Estabelecimento outroEstabelecimento;
        private Avaliacao avaliacaoOutroUsuario;

        @BeforeEach
        void setupTestesListar() {
            final UsuarioRequestDTO outroUsuarioDTO = new UsuarioRequestDTO();
            outroUsuarioDTO.setEmail("outroUsuario@email.com");
            outroUsuarioDTO.setSenha("123456");
            outroUsuarioDTO.setNome("Outro Usuario");
            outroUsuarioDTO.setUsername("outroUsuario");
            outroUsuarioDTO.setDtNascimento(LocalDate.of(1990, 5, 15));

            outroUsuario = modelMapper.map(outroUsuarioDTO, Usuario.class);
            outroUsuario.setSenha(passwordEncoder.encode(outroUsuarioDTO.getSenha()));
            usuarioRepository.save(outroUsuario);

            final EstabelecimentoRequestDTO outroEstabelecimentoDTO = new EstabelecimentoRequestDTO();
            outroEstabelecimentoDTO.setId(5L);
            outroEstabelecimentoDTO.setNome("Estabelecimento Específico");
            outroEstabelecimentoDTO.setCategoria("Cafeteria");
            outroEstabelecimentoDTO.setRua("Rua Específica, 999");
            outroEstabelecimentoDTO.setNumero("999");
            outroEstabelecimentoDTO.setBairro("Bairro Específico");
            outroEstabelecimentoDTO.setCidade("São Paulo");
            outroEstabelecimentoDTO.setEstado("SP");
            outroEstabelecimentoDTO.setCep("01234-567");

            outroEstabelecimento = modelMapper.map(outroEstabelecimentoDTO, Estabelecimento.class);
            estabelecimentoRepository.save(outroEstabelecimento);

            avaliacaoOutroUsuario = new Avaliacao();
            avaliacaoOutroUsuario.setItemConsumido("Avaliação Específica Usuario");
            avaliacaoOutroUsuario.setDescricao("Descrição usuário específico");
            avaliacaoOutroUsuario.setPreco(new BigDecimal("30.00"));
            avaliacaoOutroUsuario.setNotaGeral(4);
            avaliacaoOutroUsuario.setNotaComida(4);
            avaliacaoOutroUsuario.setNotaAtendimento(4);
            avaliacaoOutroUsuario.setNotaAmbiente(4);
            avaliacaoOutroUsuario.setEstabelecimento(estabelecimento);
            avaliacaoOutroUsuario.setUsuario(outroUsuario);
            avaliacaoRepository.save(avaliacaoOutroUsuario);

            Avaliacao avaliacaoOutroEstabelecimento = new Avaliacao();
            avaliacaoOutroEstabelecimento.setItemConsumido("Café Específico");
            avaliacaoOutroEstabelecimento.setDescricao("Descrição estabelecimento específico");
            avaliacaoOutroEstabelecimento.setPreco(new BigDecimal("15.00"));
            avaliacaoOutroEstabelecimento.setNotaGeral(3);
            avaliacaoOutroEstabelecimento.setNotaComida(3);
            avaliacaoOutroEstabelecimento.setNotaAtendimento(2);
            avaliacaoOutroEstabelecimento.setNotaAmbiente(1);
            avaliacaoOutroEstabelecimento.setEstabelecimento(outroEstabelecimento);
            avaliacaoOutroEstabelecimento.setUsuario(usuario);
            avaliacaoRepository.save(avaliacaoOutroEstabelecimento);
        }

        @Test
        void testListarComFiltroExampleSemIdUsuarioNemEstabelecimento() throws Exception {
            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("usuario", "Teste")
                            .param("estabelecimento", "Restaurante")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });
            assertNotNull(resultado);
        }

        @Test
        void testListarComIdUsuarioSomente() throws Exception {
            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("idUsuario", outroUsuario.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream()
                            .anyMatch(a -> a.getItemConsumido().equals("Avaliação Específica Usuario"))),
                    () -> assertTrue(
                            resultado.stream().allMatch(a -> a.getUsuario().getId().equals(outroUsuario.getId()))));
        }

        @Test
        void testListarComIdEstabelecimentoSomente() throws Exception {
            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("idEstabelecimento", outroEstabelecimento.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("Café Específico"))),
                    () -> assertTrue(resultado.stream()
                            .allMatch(a -> a.getEstabelecimento().getId().equals(outroEstabelecimento.getId()))));
        }

        @Test
        void testListarComIdUsuarioEIdEstabelecimento() throws Exception {
            final String responseJson = driver.perform(get(URI + "/listar")
                            .param("idUsuario", outroUsuario.getId().toString())
                            .param("idEstabelecimento", outroEstabelecimento.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertTrue(resultado.stream()
                    .allMatch(a -> a.getEstabelecimento().getId().equals(outroEstabelecimento.getId())));
        }
    }

    @Nested
    class criaAvaliacoesComFalha {
        @Test
        void testAvaliacaoComPrecoNegativo() {
            final Avaliacao avaliacaoFalha = new Avaliacao();
            avaliacaoFalha.setPreco(new BigDecimal("-5.00"));

            final Set<ConstraintViolation<Avaliacao>> violations = Validation.buildDefaultValidatorFactory()
                    .getValidator()
                    .validate(avaliacaoFalha);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
        }
    }

    @Nested
    class compressaoDeImagens {

        private byte[] gerarImagemDeTeste(int largura, int altura) throws Exception {
            final BufferedImage img = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", array);
            return array.toByteArray();
        }

        @Test
        void testComprimirAte100KBImagemGrande() throws Exception {
            byte[] imagemOriginal = gerarImagemDeTeste(1000, 1000);
            byte[] comprimida = ImageCompressor.comprimirAte100KB(imagemOriginal);

            assertTrue(comprimida.length <= 100_000);
            assertTrue(comprimida.length > 0);

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(comprimida));
            assertNotNull(img, "Imagem comprimida deve ser válida");
        }

        @Test
        void testComprimirAte100KBImagemPequena() throws Exception {
            byte[] imagemOriginal = gerarImagemDeTeste(50, 50);
            byte[] comprimida = ImageCompressor.comprimirAte100KB(imagemOriginal);

            assertArrayEquals(imagemOriginal, comprimida, "Imagem pequena não deve ser alterada");
        }

        @Test
        void testComprimirAte100KBImagemMuitoGrande() throws Exception {
            byte[] imagemOriginal = gerarImagemDeTeste(5000, 5000);
            byte[] comprimida = ImageCompressor.comprimirAte100KB(imagemOriginal);

            assertTrue(comprimida.length <= imagemOriginal.length, "Imagem comprimida deve ser menor ou igual que a original");
            assertTrue(comprimida.length > 0, "Imagem não deve estar vazia");
        }

        @Test
        void testComprimirAte100KBImagemIntegra() throws Exception {
            byte[] imagemOriginal = gerarImagemDeTeste(800, 600);
            byte[] comprimida = ImageCompressor.comprimirAte100KB(imagemOriginal);

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(comprimida));
            assertNotNull(img, "Imagem comprimida deve ser lida corretamente");
            assertEquals(800, img.getWidth(), "Largura deve ser preservada");
            assertEquals(600, img.getHeight(), "Altura deve ser preservada");
        }
    }
}
