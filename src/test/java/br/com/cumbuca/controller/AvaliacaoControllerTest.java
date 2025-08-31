package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.dto.usuario.UsuarioResponseDTO;
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
public class AvaliacaoControllerTest {
    final String URI = "/avaliacao";

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

    ModelMapper modelMapper = new ModelMapper();
    UsuarioRequestDTO usuarioRequestDTO;
    Usuario usuario;
    EstabelecimentoRequestDTO estabelecimentoRequestDTO;
    Estabelecimento estabelecimento;
    AvaliacaoRequestDTO avaliacaoRequestDTO;
    Avaliacao avaliacao;
    String token;

    @BeforeEach
    void setup() {
        // Configurar usuário
        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setEmail("testejunit@email.com");
        usuarioRequestDTO.setSenha("123456");
        usuarioRequestDTO.setNome("Teste JUnit");
        usuarioRequestDTO.setUsername("testejunit");
        usuarioRequestDTO.setDtNascimento(LocalDate.of(2000, 1, 1));

        usuario = modelMapper.map(usuarioRequestDTO, Usuario.class);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        usuarioRepository.save(usuario);

        // Configurar estabelecimento
        estabelecimentoRequestDTO = new EstabelecimentoRequestDTO();
        estabelecimentoRequestDTO.setId(1L);
        estabelecimentoRequestDTO.setNome("Restaurante Teste");
        estabelecimentoRequestDTO.setCategoria("Restaurante");
        estabelecimentoRequestDTO.setRua("Rua Teste, 123");
        estabelecimentoRequestDTO.setNumero("123");
        estabelecimentoRequestDTO.setBairro("Centro");
        estabelecimentoRequestDTO.setCidade("São Paulo");
        estabelecimentoRequestDTO.setEstado("SP");
        estabelecimentoRequestDTO.setCep("01234-567");

        estabelecimento = modelMapper.map(estabelecimentoRequestDTO, Estabelecimento.class);
        estabelecimentoRepository.save(estabelecimento);

        // Autenticar usuário
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
            final AvaliacaoRequestDTO avaliacaoRequestDTO = new AvaliacaoRequestDTO();
            avaliacaoRequestDTO.setEstabelecimento(estabelecimentoRequestDTO);
            avaliacaoRequestDTO.setItemConsumido("Pizza Margherita");
            avaliacaoRequestDTO.setDescricao("Excelente pizza, massa crocante e ingredientes frescos!");
            avaliacaoRequestDTO.setPreco(new BigDecimal("35.90"));
            avaliacaoRequestDTO.setNotaGeral(5);
            avaliacaoRequestDTO.setNotaComida(5);
            avaliacaoRequestDTO.setNotaAtendimento(4);
            avaliacaoRequestDTO.setNotaAmbiente(4);
            avaliacaoRequestDTO.setTags(Arrays.asList("pizza", "deliciosa", "recomendo"));

            final MockMultipartFile foto = new MockMultipartFile("fotos", "pizza.jpg", "image/jpeg",
                    "conteudo da foto".getBytes());

            final String responseJson = driver.perform(multipart(URI + "/criar")
                    .file(foto)
                    .param("estabelecimento.id", estabelecimentoRequestDTO.getId().toString())
                    .param("estabelecimento.nome", estabelecimentoRequestDTO.getNome())
                    .param("estabelecimento.categoria", estabelecimentoRequestDTO.getCategoria())
                    .param("estabelecimento.rua", estabelecimentoRequestDTO.getRua())
                    .param("estabelecimento.numero", estabelecimentoRequestDTO.getNumero())
                    .param("estabelecimento.bairro", estabelecimentoRequestDTO.getBairro())
                    .param("estabelecimento.cidade", estabelecimentoRequestDTO.getCidade())
                    .param("estabelecimento.estado", estabelecimentoRequestDTO.getEstado())
                    .param("estabelecimento.cep", estabelecimentoRequestDTO.getCep())
                    .param("itemConsumido", avaliacaoRequestDTO.getItemConsumido())
                    .param("descricao", avaliacaoRequestDTO.getDescricao())
                    .param("preco", avaliacaoRequestDTO.getPreco().toString())
                    .param("notaGeral", avaliacaoRequestDTO.getNotaGeral().toString())
                    .param("notaComida", avaliacaoRequestDTO.getNotaComida().toString())
                    .param("notaAtendimento", avaliacaoRequestDTO.getNotaAtendimento().toString())
                    .param("notaAmbiente", avaliacaoRequestDTO.getNotaAmbiente().toString())
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
                    () -> assertEquals(avaliacaoRequestDTO.getItemConsumido(), resultado.getItemConsumido()),
                    () -> assertEquals(avaliacaoRequestDTO.getDescricao(), resultado.getDescricao()),
                    () -> assertEquals(avaliacaoRequestDTO.getPreco(), resultado.getPreco()),
                    () -> assertEquals(avaliacaoRequestDTO.getNotaGeral(), resultado.getNotaGeral()),
                    () -> assertEquals(avaliacaoRequestDTO.getNotaComida(), resultado.getNotaComida()),
                    () -> assertEquals(avaliacaoRequestDTO.getNotaAtendimento(), resultado.getNotaAtendimento()),
                    () -> assertEquals(avaliacaoRequestDTO.getNotaAmbiente(), resultado.getNotaAmbiente()),
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
                    .param("estabelecimento.id", estabelecimentoRequestDTO.getId().toString())
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
                    .with(request -> { request.setMethod("PUT"); return request; })
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
                     () -> assertEquals(avaliacao.getNotaComida(), resultado.getNotaComida())
             );
        }

        @Test
        void testListarAvalicaoes() throws Exception {
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


             final List<AvaliacaoResponseDTO> resultado =
             objectMapper.readValue(responseJson, new TypeReference<>() {
             });

             assertAll(
             () -> assertTrue(resultado.stream().anyMatch(a ->
             a.getDescricao().equals(avaliacao1.getDescricao()))),
             () -> assertTrue(resultado.stream().anyMatch(a ->
             a.getDescricao().equals(avaliacao2.getDescricao())))
             );
        }
    }
}
