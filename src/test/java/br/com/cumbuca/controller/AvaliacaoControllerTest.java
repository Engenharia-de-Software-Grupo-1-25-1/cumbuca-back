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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        estabelecimentoRequestDTO.setNome("Restaurante Teste");
        estabelecimentoRequestDTO.setCategoria("Restaurante");
        estabelecimentoRequestDTO.setRua("Rua Teste, 123");
        estabelecimentoRequestDTO.setNumero("123");
        estabelecimentoRequestDTO.setBairro("Centro");
        estabelecimentoRequestDTO.setCidade("Campina Grande");
        estabelecimentoRequestDTO.setEstado("PB");
        estabelecimentoRequestDTO.setCep("01234-567");

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
        avaliacaoRequestDTO.setNotaGeral(5);
        avaliacaoRequestDTO.setNotaComida(5);
        avaliacaoRequestDTO.setNotaAtendimento(4);
        avaliacaoRequestDTO.setNotaAmbiente(4);
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
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
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
            avaliacaoAtualizadaDTO.setNotaGeral(4);
            avaliacaoAtualizadaDTO.setNotaComida(4);
            avaliacaoAtualizadaDTO.setNotaAtendimento(5);
            avaliacaoAtualizadaDTO.setNotaAmbiente(4);
            avaliacaoAtualizadaDTO.setTags(Arrays.asList("hambúrguer", "artesanal", "saboroso"));

            final MockMultipartFile foto = new MockMultipartFile("fotos", "hamburguer.jpg", "image/jpeg",
                    "conteudo da foto atualizada".getBytes());

            final String responseJson = driver.perform(multipart(URI + "/atualizar/" + avaliacao.getId())
                    .file(foto)
                    .param("estabelecimento.id", estabelecimento.getId().toString())
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoAtualizadaDTO.getItemConsumido())
                    .param("descricao", avaliacaoAtualizadaDTO.getDescricao())
                    .param("preco", avaliacaoAtualizadaDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoAtualizadaDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoAtualizadaDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoAtualizadaDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoAtualizadaDTO.getNotaAmbiente().toString())
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
                    () -> assertEquals(avaliacaoAtualizadaDTO.getNotaGeral(), resultado.getNotaGeral()),
                    () -> assertEquals(avaliacaoAtualizadaDTO.getNotaComida(), resultado.getNotaComida()),
                    () -> assertEquals(avaliacaoAtualizadaDTO.getNotaAtendimento(), resultado.getNotaAtendimento()),
                    () -> assertEquals(avaliacaoAtualizadaDTO.getNotaAmbiente(), resultado.getNotaAmbiente()),
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
            avaliacao1.setNotaGeral(4);
            avaliacao1.setNotaComida(4);
            avaliacao1.setNotaAtendimento(5);
            avaliacao1.setNotaAmbiente(3);
            avaliacao1.setEstabelecimento(estabelecimento);
            avaliacao1.setUsuario(usuario);

            final Avaliacao avaliacao2 = new Avaliacao();
            avaliacao2.setItemConsumido("Hambúrguer");
            avaliacao2.setDescricao("Hambúrguer delicioso");
            avaliacao2.setPreco(new BigDecimal("35.00"));
            avaliacao2.setNotaGeral(5);
            avaliacao2.setNotaComida(5);
            avaliacao2.setNotaAtendimento(4);
            avaliacao2.setNotaAmbiente(2);
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
            avaliacaoOutroUsuario.setNotaGeral(4);
            avaliacaoOutroUsuario.setNotaComida(4);
            avaliacaoOutroUsuario.setNotaAtendimento(3);
            avaliacaoOutroUsuario.setNotaAmbiente(4);
            avaliacaoOutroUsuario.setEstabelecimento(estabelecimento);
            avaliacaoOutroUsuario.setUsuario(outroUsuario);
            avaliacaoRepository.save(avaliacaoOutroUsuario);

            final AvaliacaoRequestDTO avaliacaoAtualizadaDTO = new AvaliacaoRequestDTO();
            avaliacaoAtualizadaDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoAtualizadaDTO.setItemConsumido("Pizza Atualizada");
            avaliacaoAtualizadaDTO.setDescricao("Tentando atualizar avaliação de outro usuário");
            avaliacaoAtualizadaDTO.setPreco(new BigDecimal("35.00"));
            avaliacaoAtualizadaDTO.setNotaGeral(5);
            avaliacaoAtualizadaDTO.setNotaComida(5);
            avaliacaoAtualizadaDTO.setNotaAtendimento(5);
            avaliacaoAtualizadaDTO.setNotaAmbiente(5);

            final String responseText = driver.perform(multipart(URI + "/atualizar/" + avaliacaoOutroUsuario.getId())
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoAtualizadaDTO.getItemConsumido())
                    .param("descricao", avaliacaoAtualizadaDTO.getDescricao())
                    .param("preco", avaliacaoAtualizadaDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoAtualizadaDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoAtualizadaDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoAtualizadaDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoAtualizadaDTO.getNotaAmbiente().toString())
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
            avaliacaoOutroUsuario.setNotaGeral(5);
            avaliacaoOutroUsuario.setNotaComida(5);
            avaliacaoOutroUsuario.setNotaAtendimento(4);
            avaliacaoOutroUsuario.setNotaAmbiente(4);
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
            avaliacaoBarata.setNotaGeral(4);
            avaliacaoBarata.setNotaComida(4);
            avaliacaoBarata.setNotaAtendimento(3);
            avaliacaoBarata.setNotaAmbiente(3);
            avaliacaoBarata.setEstabelecimento(estabelecimento);
            avaliacaoBarata.setUsuario(usuario);

            final Avaliacao avaliacaoCara = new Avaliacao();
            avaliacaoCara.setItemConsumido("Prato Executivo");
            avaliacaoCara.setDescricao("Prato mais caro mas muito bom");
            avaliacaoCara.setPreco(new BigDecimal("45.00"));
            avaliacaoCara.setNotaGeral(5);
            avaliacaoCara.setNotaComida(5);
            avaliacaoCara.setNotaAtendimento(5);
            avaliacaoCara.setNotaAmbiente(4);
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

        @Test
        void testFiltrarAvaliacoesPorPrecoMaximo() throws Exception {
            final Avaliacao avaliacaoBarata = new Avaliacao();
            avaliacaoBarata.setItemConsumido("Lanche Simples");
            avaliacaoBarata.setDescricao("Lanche barato e gostoso");
            avaliacaoBarata.setPreco(new BigDecimal("15.00"));
            avaliacaoBarata.setNotaGeral(4);
            avaliacaoBarata.setNotaComida(4);
            avaliacaoBarata.setNotaAtendimento(3);
            avaliacaoBarata.setNotaAmbiente(3);
            avaliacaoBarata.setEstabelecimento(estabelecimento);
            avaliacaoBarata.setUsuario(usuario);

            final Avaliacao avaliacaoCara = new Avaliacao();
            avaliacaoCara.setItemConsumido("Prato Executivo");
            avaliacaoCara.setDescricao("Prato mais caro mas muito bom");
            avaliacaoCara.setPreco(new BigDecimal("45.00"));
            avaliacaoCara.setNotaGeral(5);
            avaliacaoCara.setNotaComida(5);
            avaliacaoCara.setNotaAtendimento(5);
            avaliacaoCara.setNotaAmbiente(4);
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
        void testFiltrarAvaliacoesPorFaixaDePreco() throws Exception {
            final Avaliacao avaliacaoBarata = new Avaliacao();
            avaliacaoBarata.setItemConsumido("Lanche Simples");
            avaliacaoBarata.setDescricao("Lanche barato e gostoso");
            avaliacaoBarata.setPreco(new BigDecimal("15.00"));
            avaliacaoBarata.setNotaGeral(4);
            avaliacaoBarata.setNotaComida(4);
            avaliacaoBarata.setNotaAtendimento(3);
            avaliacaoBarata.setNotaAmbiente(3);
            avaliacaoBarata.setEstabelecimento(estabelecimento);
            avaliacaoBarata.setUsuario(usuario);

            final Avaliacao avaliacaoMedia = new Avaliacao();
            avaliacaoMedia.setItemConsumido("Prato Principal");
            avaliacaoMedia.setDescricao("Prato com preço justo");
            avaliacaoMedia.setPreco(new BigDecimal("30.00"));
            avaliacaoMedia.setNotaGeral(4);
            avaliacaoMedia.setNotaComida(4);
            avaliacaoMedia.setNotaAtendimento(4);
            avaliacaoMedia.setNotaAmbiente(3);
            avaliacaoMedia.setEstabelecimento(estabelecimento);
            avaliacaoMedia.setUsuario(usuario);

            final Avaliacao avaliacaoCara = new Avaliacao();
            avaliacaoCara.setItemConsumido("Prato Executivo");
            avaliacaoCara.setDescricao("Prato mais caro mas muito bom");
            avaliacaoCara.setPreco(new BigDecimal("45.00"));
            avaliacaoCara.setNotaGeral(5);
            avaliacaoCara.setNotaComida(5);
            avaliacaoCara.setNotaAtendimento(5);
            avaliacaoCara.setNotaAmbiente(4);
            avaliacaoCara.setEstabelecimento(estabelecimento);
            avaliacaoCara.setUsuario(usuario);

            avaliacaoRepository.saveAll(Arrays.asList(avaliacaoBarata, avaliacaoMedia, avaliacaoCara));

            final String responseJson = driver.perform(get(URI + "/listar")
                    .param("precoMinimo", "20.00")
                    .param("precoMaximo", "35.00")
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
                            resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("30.00")) == 0)),
                    () -> assertFalse(
                            resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("15.00")) == 0)),
                    () -> assertFalse(
                            resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("45.00")) == 0)));
        }

        @Test
        void testFiltrarAvaliacoesSemFiltroDePreco() throws Exception {
            final Avaliacao avaliacaoBarata = new Avaliacao();
            avaliacaoBarata.setItemConsumido("Lanche Simples");
            avaliacaoBarata.setDescricao("Lanche barato e gostoso");
            avaliacaoBarata.setPreco(new BigDecimal("15.00"));
            avaliacaoBarata.setNotaGeral(4);
            avaliacaoBarata.setNotaComida(4);
            avaliacaoBarata.setNotaAtendimento(3);
            avaliacaoBarata.setNotaAmbiente(3);
            avaliacaoBarata.setEstabelecimento(estabelecimento);
            avaliacaoBarata.setUsuario(usuario);

            final Avaliacao avaliacaoCara = new Avaliacao();
            avaliacaoCara.setItemConsumido("Prato Executivo");
            avaliacaoCara.setDescricao("Prato mais caro mas muito bom");
            avaliacaoCara.setPreco(new BigDecimal("45.00"));
            avaliacaoCara.setNotaGeral(5);
            avaliacaoCara.setNotaComida(5);
            avaliacaoCara.setNotaAtendimento(5);
            avaliacaoCara.setNotaAmbiente(4);
            avaliacaoCara.setEstabelecimento(estabelecimento);
            avaliacaoCara.setUsuario(usuario);

            avaliacaoRepository.saveAll(Arrays.asList(avaliacaoBarata, avaliacaoCara));

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
                            resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("15.00")) == 0)),
                    () -> assertTrue(
                            resultado.stream().anyMatch(a -> a.getPreco().compareTo(new BigDecimal("45.00")) == 0)));
        }

        @Test
        void testFiltrarAvaliacoesPorTags() throws Exception {
            final AvaliacaoRequestDTO avaliacaoComTagDTO = new AvaliacaoRequestDTO();
            avaliacaoComTagDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoComTagDTO.setItemConsumido("Pizza Margherita");
            avaliacaoComTagDTO.setDescricao("Pizza deliciosa!");
            avaliacaoComTagDTO.setPreco(new BigDecimal("28.00"));
            avaliacaoComTagDTO.setNotaGeral(5);
            avaliacaoComTagDTO.setNotaComida(5);
            avaliacaoComTagDTO.setNotaAtendimento(4);
            avaliacaoComTagDTO.setNotaAmbiente(4);
            avaliacaoComTagDTO.setTags(Arrays.asList("pizza", "italiana", "queijo"));

            final MockMultipartFile foto1 = new MockMultipartFile("fotos", "pizza.jpg", "image/jpeg",
                    "conteudo da foto".getBytes());

            driver.perform(multipart(URI + "/criar")
                    .file(foto1)
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoComTagDTO.getItemConsumido())
                    .param("descricao", avaliacaoComTagDTO.getDescricao())
                    .param("preco", avaliacaoComTagDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoComTagDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoComTagDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoComTagDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoComTagDTO.getNotaAmbiente().toString())
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
            avaliacaoSemTagDTO.setNotaGeral(4);
            avaliacaoSemTagDTO.setNotaComida(4);
            avaliacaoSemTagDTO.setNotaAtendimento(3);
            avaliacaoSemTagDTO.setNotaAmbiente(3);
            avaliacaoSemTagDTO.setTags(Arrays.asList("hambúrguer", "carne", "pão"));

            final MockMultipartFile foto2 = new MockMultipartFile("fotos", "hamburguer.jpg", "image/jpeg",
                    "conteudo da foto".getBytes());

            driver.perform(multipart(URI + "/criar")
                    .file(foto2)
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoSemTagDTO.getItemConsumido())
                    .param("descricao", avaliacaoSemTagDTO.getDescricao())
                    .param("preco", avaliacaoSemTagDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoSemTagDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoSemTagDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoSemTagDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoSemTagDTO.getNotaAmbiente().toString())
                    .param("tags", "hambúrguer", "carne", "pão")
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
        void testFiltrarAvaliacoesSemTags() throws Exception {
            final AvaliacaoRequestDTO avaliacaoComTagDTO = new AvaliacaoRequestDTO();
            avaliacaoComTagDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoComTagDTO.setItemConsumido("Lasanha");
            avaliacaoComTagDTO.setDescricao("Lasanha deliciosa!");
            avaliacaoComTagDTO.setPreco(new BigDecimal("35.00"));
            avaliacaoComTagDTO.setNotaGeral(5);
            avaliacaoComTagDTO.setNotaComida(5);
            avaliacaoComTagDTO.setNotaAtendimento(4);
            avaliacaoComTagDTO.setNotaAmbiente(4);
            avaliacaoComTagDTO.setTags(Arrays.asList("lasanha", "massa", "molho"));

            final MockMultipartFile foto = new MockMultipartFile("fotos", "lasanha.jpg", "image/jpeg",
                    "conteudo da foto".getBytes());

            driver.perform(multipart(URI + "/criar")
                    .file(foto)
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoComTagDTO.getItemConsumido())
                    .param("descricao", avaliacaoComTagDTO.getDescricao())
                    .param("preco", avaliacaoComTagDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoComTagDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoComTagDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoComTagDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoComTagDTO.getNotaAmbiente().toString())
                    .param("tags", "lasanha", "massa", "molho")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("Authorization", "Bearer " + token)
                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isCreated());

            final String responseJsonSemFiltro = driver.perform(get(URI + "/listar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultadoSemFiltro = objectMapper.readValue(responseJsonSemFiltro,
                    new TypeReference<>() {
                    });

            assertTrue(resultadoSemFiltro.stream().anyMatch(a -> a.getItemConsumido().equals("Lasanha")));
        }

        @Test
        void testFiltrarAvaliacoesPorTagCaseInsensitive() throws Exception {
            final AvaliacaoRequestDTO avaliacaoDTO = new AvaliacaoRequestDTO();
            avaliacaoDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoDTO.setItemConsumido("Risotto");
            avaliacaoDTO.setDescricao("Risotto cremoso!");
            avaliacaoDTO.setPreco(new BigDecimal("38.00"));
            avaliacaoDTO.setNotaGeral(5);
            avaliacaoDTO.setNotaComida(5);
            avaliacaoDTO.setNotaAtendimento(4);
            avaliacaoDTO.setNotaAmbiente(4);
            avaliacaoDTO.setTags(Arrays.asList("RISOTTO", "Italiano", "cremoso"));

            final MockMultipartFile foto = new MockMultipartFile("fotos", "risotto.jpg", "image/jpeg",
                    "conteudo da foto".getBytes());

            driver.perform(multipart(URI + "/criar")
                    .file(foto)
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoDTO.getItemConsumido())
                    .param("descricao", avaliacaoDTO.getDescricao())
                    .param("preco", avaliacaoDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoDTO.getNotaAmbiente().toString())
                    .param("tags", "RISOTTO", "Italiano", "cremoso")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("Authorization", "Bearer " + token)
                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isCreated());

            final String responseJsonFiltrado = driver.perform(get(URI + "/listar")
                    .param("tags", "risotto")
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

            assertTrue(resultadoFiltrado.stream().anyMatch(a -> a.getItemConsumido().equals("Risotto")));
        }
    }

    @Nested
    class FiltrosPorUsuarioEEstabelecimento {

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
            avaliacaoOutroUsuario.setNotaGeral(5);
            avaliacaoOutroUsuario.setNotaComida(5);
            avaliacaoOutroUsuario.setNotaAtendimento(4);
            avaliacaoOutroUsuario.setNotaAmbiente(4);
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
        void testFiltrarAvaliacoesPorNomeUsuarioCompleto() throws Exception {
            final UsuarioRequestDTO outroUsuarioDTO = new UsuarioRequestDTO();
            outroUsuarioDTO.setEmail("mariasilva@email.com");
            outroUsuarioDTO.setSenha("123456");
            outroUsuarioDTO.setNome("Maria da Silva");
            outroUsuarioDTO.setUsername("mariasilva");
            outroUsuarioDTO.setDtNascimento(LocalDate.of(1990, 8, 20));

            final Usuario outroUsuario = modelMapper.map(outroUsuarioDTO, Usuario.class);
            outroUsuario.setSenha(passwordEncoder.encode(outroUsuarioDTO.getSenha()));
            usuarioRepository.save(outroUsuario);

            final Avaliacao avaliacaoOutroUsuario = new Avaliacao();
            avaliacaoOutroUsuario.setItemConsumido("Moqueca");
            avaliacaoOutroUsuario.setDescricao("Moqueca saborosa!");
            avaliacaoOutroUsuario.setPreco(new BigDecimal("40.00"));
            avaliacaoOutroUsuario.setNotaGeral(4);
            avaliacaoOutroUsuario.setNotaComida(4);
            avaliacaoOutroUsuario.setNotaAtendimento(3);
            avaliacaoOutroUsuario.setNotaAmbiente(4);
            avaliacaoOutroUsuario.setEstabelecimento(estabelecimento);
            avaliacaoOutroUsuario.setUsuario(outroUsuario);
            avaliacaoRepository.save(avaliacaoOutroUsuario);

            final String responseJson = driver.perform(get(URI + "/listar")
                    .param("usuario", "Maria da Silva")
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
                    () -> assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("Moqueca"))),
                    () -> assertTrue(resultado.stream().allMatch(a -> a.getUsuario().getNome().contains("Maria"))));
        }

        @Test
        void testFiltrarAvaliacoesPorNomeEstabelecimento() throws Exception {
            final EstabelecimentoRequestDTO outroEstabelecimentoDTO = new EstabelecimentoRequestDTO();
            outroEstabelecimentoDTO.setNome("Pizzaria Bella Vista");
            outroEstabelecimentoDTO.setCategoria("Pizzaria");
            outroEstabelecimentoDTO.setRua("Rua das Flores, 456");
            outroEstabelecimentoDTO.setNumero("456");
            outroEstabelecimentoDTO.setBairro("Jardim");
            outroEstabelecimentoDTO.setCidade("São Paulo");
            outroEstabelecimentoDTO.setEstado("SP");
            outroEstabelecimentoDTO.setCep("01234-567");

            final Estabelecimento outroEstabelecimento = modelMapper.map(outroEstabelecimentoDTO,
                    Estabelecimento.class);
            estabelecimentoRepository.save(outroEstabelecimento);

            final Avaliacao avaliacaoOutroEstabelecimento = new Avaliacao();
            avaliacaoOutroEstabelecimento.setItemConsumido("Pizza Napoletana");
            avaliacaoOutroEstabelecimento.setDescricao("Pizza autêntica italiana!");
            avaliacaoOutroEstabelecimento.setPreco(new BigDecimal("42.00"));
            avaliacaoOutroEstabelecimento.setNotaGeral(5);
            avaliacaoOutroEstabelecimento.setNotaComida(5);
            avaliacaoOutroEstabelecimento.setNotaAtendimento(5);
            avaliacaoOutroEstabelecimento.setNotaAmbiente(4);
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

        @Test
        void testFiltrarAvaliacoesPorUsuarioEEstabelecimento() throws Exception {
            final UsuarioRequestDTO outroUsuarioDTO = new UsuarioRequestDTO();
            outroUsuarioDTO.setEmail("carlos@email.com");
            outroUsuarioDTO.setSenha("123456");
            outroUsuarioDTO.setNome("Carlos Pereira");
            outroUsuarioDTO.setUsername("carlospereira");
            outroUsuarioDTO.setDtNascimento(LocalDate.of(1988, 12, 10));

            final Usuario outroUsuario = modelMapper.map(outroUsuarioDTO, Usuario.class);
            outroUsuario.setSenha(passwordEncoder.encode(outroUsuarioDTO.getSenha()));
            usuarioRepository.save(outroUsuario);

            final EstabelecimentoRequestDTO outroEstabelecimentoDTO = new EstabelecimentoRequestDTO();
            outroEstabelecimentoDTO.setNome("Burger House");
            outroEstabelecimentoDTO.setCategoria("Hamburgueria");
            outroEstabelecimentoDTO.setRua("Rua Principal, 789");
            outroEstabelecimentoDTO.setNumero("789");
            outroEstabelecimentoDTO.setBairro("Centro");
            outroEstabelecimentoDTO.setCidade("São Paulo");
            outroEstabelecimentoDTO.setEstado("SP");
            outroEstabelecimentoDTO.setCep("01234-567");

            final Estabelecimento outroEstabelecimento = modelMapper.map(outroEstabelecimentoDTO,
                    Estabelecimento.class);
            estabelecimentoRepository.save(outroEstabelecimento);

            final Avaliacao avaliacaoEspecifica = new Avaliacao();
            avaliacaoEspecifica.setItemConsumido("Burger Gourmet");
            avaliacaoEspecifica.setDescricao("Burger incrível!");
            avaliacaoEspecifica.setPreco(new BigDecimal("38.00"));
            avaliacaoEspecifica.setNotaGeral(5);
            avaliacaoEspecifica.setNotaComida(5);
            avaliacaoEspecifica.setNotaAtendimento(4);
            avaliacaoEspecifica.setNotaAmbiente(4);
            avaliacaoEspecifica.setEstabelecimento(outroEstabelecimento);
            avaliacaoEspecifica.setUsuario(outroUsuario);
            avaliacaoRepository.save(avaliacaoEspecifica);

            final String responseJson = driver.perform(get(URI + "/listar")
                    .param("usuario", "Carlos")
                    .param("estabelecimento", "House")
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
                    () -> assertEquals(1, resultado.size()),
                    () -> assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("Burger Gourmet"))),
                    () -> assertTrue(resultado.stream().allMatch(a -> a.getUsuario().getNome().contains("Carlos"))),
                    () -> assertTrue(
                            resultado.stream().allMatch(a -> a.getEstabelecimento().getNome().contains("House"))));
        }

        @Test
        void testFiltrarAvaliacoesCaseInsensitive() throws Exception {
            final UsuarioRequestDTO usuarioUpperDTO = new UsuarioRequestDTO();
            usuarioUpperDTO.setEmail("ana@email.com");
            usuarioUpperDTO.setSenha("123456");
            usuarioUpperDTO.setNome("ANA COSTA");
            usuarioUpperDTO.setUsername("anacosta");
            usuarioUpperDTO.setDtNascimento(LocalDate.of(1992, 3, 25));

            final Usuario usuarioUpper = modelMapper.map(usuarioUpperDTO, Usuario.class);
            usuarioUpper.setSenha(passwordEncoder.encode(usuarioUpperDTO.getSenha()));
            usuarioRepository.save(usuarioUpper);

            final Avaliacao avaliacaoUpper = new Avaliacao();
            avaliacaoUpper.setItemConsumido("Salada Caesar");
            avaliacaoUpper.setDescricao("Salada fresca!");
            avaliacaoUpper.setPreco(new BigDecimal("22.00"));
            avaliacaoUpper.setNotaGeral(4);
            avaliacaoUpper.setNotaComida(4);
            avaliacaoUpper.setNotaAtendimento(4);
            avaliacaoUpper.setNotaAmbiente(3);
            avaliacaoUpper.setEstabelecimento(estabelecimento);
            avaliacaoUpper.setUsuario(usuarioUpper);
            avaliacaoRepository.save(avaliacaoUpper);

            final String responseJson = driver.perform(get(URI + "/listar")
                    .param("usuario", "ana costa")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("Salada Caesar")));
        }

        @Test
        void testFiltrarAvaliacoesSemFiltros() throws Exception {
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

            assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("picado")));
        }

        @Test
        void testFiltrarAvaliacoesComFiltroVazio() throws Exception {
            final String responseJson = driver.perform(get(URI + "/listar")
                    .param("usuario", "")
                    .param("estabelecimento", "")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("picado")));
        }
    }

    @Nested
    class TestesCenarioErro {

        @Test
        void testRecuperarAvaliacaoInexistente() throws Exception {
            final long idInexistente = 99999L;

            driver.perform(get(URI + "/recuperar/" + idInexistente)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        void testAtualizarAvaliacaoInexistente() throws Exception {
            final long idInexistente = 99999L;
            final AvaliacaoRequestDTO avaliacaoDTO = new AvaliacaoRequestDTO();
            avaliacaoDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoDTO.setItemConsumido("Item Teste");
            avaliacaoDTO.setDescricao("Descrição teste");
            avaliacaoDTO.setPreco(new BigDecimal("25.00"));
            avaliacaoDTO.setNotaGeral(4);
            avaliacaoDTO.setNotaComida(4);
            avaliacaoDTO.setNotaAtendimento(3);
            avaliacaoDTO.setNotaAmbiente(3);

            driver.perform(multipart(URI + "/atualizar/" + idInexistente)
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoDTO.getItemConsumido())
                    .param("descricao", avaliacaoDTO.getDescricao())
                    .param("preco", avaliacaoDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoDTO.getNotaAmbiente().toString())
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    })
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("Authorization", "Bearer " + token)
                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        void testRemoverAvaliacaoInexistente() throws Exception {
            final long idInexistente = 99999L;

            driver.perform(delete(URI + "/remover/" + idInexistente)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        void testAcessoSemAutenticacao() throws Exception {
            driver.perform(get(URI + "/recuperar/" + avaliacao.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        void testAcessoComTokenInvalido() throws Exception {
            driver.perform(get(URI + "/recuperar/" + avaliacao.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer tokenInvalido"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }


    @Nested
    class TestesCriacaoComFotosETags {

        @Test
        void testCriarAvaliacaoSemFotos() throws Exception {
            final AvaliacaoRequestDTO avaliacaoDTO = new AvaliacaoRequestDTO();
            avaliacaoDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoDTO.setItemConsumido("Sem Fotos");
            avaliacaoDTO.setDescricao("Avaliação sem fotos");
            avaliacaoDTO.setPreco(new BigDecimal("20.00"));
            avaliacaoDTO.setNotaGeral(4);
            avaliacaoDTO.setNotaComida(4);
            avaliacaoDTO.setNotaAtendimento(3);
            avaliacaoDTO.setNotaAmbiente(3);

            final String responseJson = driver.perform(multipart(URI + "/criar")
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoDTO.getItemConsumido())
                    .param("descricao", avaliacaoDTO.getDescricao())
                    .param("preco", avaliacaoDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoDTO.getNotaAmbiente().toString())
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
                    () -> assertEquals(avaliacaoDTO.getItemConsumido(), resultado.getItemConsumido()),
                    () -> assertEquals(avaliacaoDTO.getDescricao(), resultado.getDescricao()));
        }

        @Test
        void testCriarAvaliacaoSemTags() throws Exception {
            final AvaliacaoRequestDTO avaliacaoDTO = new AvaliacaoRequestDTO();
            avaliacaoDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoDTO.setItemConsumido("Sem Tags");
            avaliacaoDTO.setDescricao("Avaliação sem tags");
            avaliacaoDTO.setPreco(new BigDecimal("25.00"));
            avaliacaoDTO.setNotaGeral(3);
            avaliacaoDTO.setNotaComida(3);
            avaliacaoDTO.setNotaAtendimento(3);
            avaliacaoDTO.setNotaAmbiente(3);

            final MockMultipartFile foto = new MockMultipartFile("fotos", "teste.jpg", "image/jpeg",
                    "conteudo da foto".getBytes());

            final String responseJson = driver.perform(multipart(URI + "/criar")
                    .file(foto)
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoDTO.getItemConsumido())
                    .param("descricao", avaliacaoDTO.getDescricao())
                    .param("preco", avaliacaoDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoDTO.getNotaAmbiente().toString())
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
                    () -> assertEquals(avaliacaoDTO.getItemConsumido(), resultado.getItemConsumido()),
                    () -> assertEquals(avaliacaoDTO.getDescricao(), resultado.getDescricao()));
        }

        @Test
        void testAtualizarAvaliacaoSemFotosESemTags() throws Exception {
            final AvaliacaoRequestDTO avaliacaoDTO = new AvaliacaoRequestDTO();
            avaliacaoDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoDTO.setItemConsumido("Atualizada Sem Anexos");
            avaliacaoDTO.setDescricao("Avaliação atualizada sem fotos e tags");
            avaliacaoDTO.setPreco(new BigDecimal("30.00"));
            avaliacaoDTO.setNotaGeral(4);
            avaliacaoDTO.setNotaComida(4);
            avaliacaoDTO.setNotaAtendimento(4);
            avaliacaoDTO.setNotaAmbiente(4);

            final String responseJson = driver.perform(multipart(URI + "/atualizar/" + avaliacao.getId())
                    .param("estabelecimento.id", estabelecimento.getId().toString())
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoDTO.getItemConsumido())
                    .param("descricao", avaliacaoDTO.getDescricao())
                    .param("preco", avaliacaoDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoDTO.getNotaAmbiente().toString())
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
                    () -> assertEquals(avaliacaoDTO.getItemConsumido(), resultado.getItemConsumido()),
                    () -> assertEquals(avaliacaoDTO.getDescricao(), resultado.getDescricao()));
        }
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

        @Test
        void testListarComFiltrosDeNotasCompletos() throws Exception {
            final String responseJson = driver.perform(get(URI + "/listar")
                    .param("notaGeral", "3")
                    .param("notaComida", "3")
                    .param("notaAtendimento", "2")
                    .param("notaAmbiente", "1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<AvaliacaoResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("Café Específico")));
        }

        @Test
        void testListarSemFiltros() throws Exception {
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
                    () -> assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("picado"))),
                    () -> assertTrue(resultado.stream().anyMatch(a -> a.getItemConsumido().equals("Café Específico"))));
        }
    }
}
