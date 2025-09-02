package br.com.cumbuca.controller;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.estabelecimento.EstabelecimentoRequestDTO;
import br.com.cumbuca.dto.tag.TagResponseDTO;
import br.com.cumbuca.dto.usuario.UsuarioRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Estabelecimento;
import br.com.cumbuca.model.Tag;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import br.com.cumbuca.repository.EstabelecimentoRepository;
import br.com.cumbuca.repository.TagRepository;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.autenticacao.TokenService;
import br.com.cumbuca.service.tag.TagServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TagControllerTest {
    static final String URI = "/tag";

    @Autowired
    MockMvc driver;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    ModelMapper modelMapper = new ModelMapper();
    Usuario usuario;
    Avaliacao avaliacao;
    Estabelecimento estabelecimento;
    UsuarioRequestDTO usuarioRequestDTO;
    AvaliacaoRequestDTO avaliacaoRequestDTO;
    EstabelecimentoRequestDTO estabelecimentoRequestDTO;
    String token;

    @BeforeEach
    void setUp() {
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

        Tag tag1 = new Tag();
        tag1.setConteudo("jantar");
        tag1.setAvaliacao(avaliacao);
        Tag tag2 = new Tag();
        tag2.setConteudo("amigos");
        tag2.setAvaliacao(avaliacao);
        tagRepository.saveAll(Arrays.asList(tag1, tag2));
    }

    @AfterEach
    void tearDown() {
        tagRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Nested
    class TagFluxoBasicoApiRest {
        @Test
        void testListarTags() throws Exception {
            final String responseJson = driver.perform(get(URI + "/listar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);


            final List<TagResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {
            });

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(t -> t.getTag().equals("jantar"))),
                    () -> assertTrue(resultado.stream().anyMatch(t -> t.getTag().equals("amigos")))
            );
        }

        @Test
        void testListarTagsPopulares() throws Exception {
            AvaliacaoRequestDTO avaliacaoRequestDTO2 = new AvaliacaoRequestDTO();
            avaliacaoRequestDTO2.setItemConsumido("pizza");
            avaliacaoRequestDTO2.setDescricao("muito boa");
            avaliacaoRequestDTO2.setPreco(new BigDecimal("25.00"));
            Avaliacao avaliacao2 = modelMapper.map(avaliacaoRequestDTO2, Avaliacao.class);
            avaliacao2.setEstabelecimento(estabelecimento);
            avaliacao2.setUsuario(usuario);
            avaliacaoRepository.save(avaliacao2);
            Tag tagExtra = new Tag();
            tagExtra.setConteudo("jantar");
            tagExtra.setAvaliacao(avaliacao2);
            tagRepository.saveAll(Arrays.asList(tagExtra));
            avaliacaoRequestDTO2.setTags(List.of(tagExtra.getConteudo()));
            avaliacaoRepository.save(avaliacao2);

            final String responseJson = driver.perform(get(URI + "/populares/listar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            final List<TagResponseDTO> resultado = objectMapper.readValue(responseJson, new TypeReference<>() {});

            assertAll(
                    () -> assertTrue(resultado.stream().anyMatch(t -> t.getTag().equals("jantar"))),
                    () -> assertTrue(resultado.stream().anyMatch(t -> t.getTag().equals("amigos"))),
                    () -> assertEquals(2, resultado.stream()
                            .filter(t -> t.getTag().equals("jantar"))
                            .findFirst()
                            .get()
                            .getQuantidade()),
                    () -> assertEquals(1, resultado.stream()
                            .filter(t -> t.getTag().equals("amigos"))
                            .findFirst()
                            .get()
                            .getQuantidade())
            );
        }

        @Test
        void testNormalizarTagComNull() {
            assertNull(TagServiceImpl.normalizarTag(null));
        }
    }

}