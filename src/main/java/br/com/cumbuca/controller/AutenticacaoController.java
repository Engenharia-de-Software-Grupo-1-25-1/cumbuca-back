package br.com.cumbuca.controller;

import br.com.cumbuca.dto.login.LoginRequestDTO;
import br.com.cumbuca.dto.login.MailBody;
import br.com.cumbuca.dto.login.RecuperarSenhaRequestDTO;
import br.com.cumbuca.dto.login.RecuperarSenhaResponseDTO;
import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.UsuarioRepository;
import br.com.cumbuca.service.autenticacao.EmailService;
import br.com.cumbuca.service.autenticacao.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO login) {
        final UsernamePasswordAuthenticationToken autenticationToken = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getSenha());
        final Authentication authentication = authenticationManager.authenticate(autenticationToken);
        final String tokenAcesso = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(tokenAcesso);
    }

    @PostMapping("/recuperarSenha")
    public ResponseEntity<String> recuperarSenha(@Valid @RequestBody RecuperarSenhaRequestDTO recuperarSenha) throws Exception {
        Optional<Usuario> usuarioBuscado = usuarioRepository.findByUsernameOrEmail(recuperarSenha.getEmail(), recuperarSenha.getEmail());
        if (usuarioBuscado.isEmpty()) return ResponseEntity.status(404).body("Usuário não encontrado.");

        Usuario usuario = usuarioBuscado.get();
        String token = tokenService.gerarToken(usuario);
        String link = "http://localhost:8080/swagger-ui/index.html#/autenticacao-controller/alterar-senha?token=" + token;

        MailBody mail = MailBody.builder()
                .to(recuperarSenha.getEmail())
                .subject("Recuperação de senha do Cumbuca")
                .text("Clique no link para redefinir sua senha: " + link)
                .build();

        emailService.sendSimpleMessage(mail);

        return ResponseEntity.ok("E-mail enviado.");
    }

    @PostMapping("/alterarSenha")
    public ResponseEntity<String> alterarSenha(@RequestBody RecuperarSenhaResponseDTO dto) {
        if (!dto.getNovaSenha().equals(dto.getConfirmarSenha())) {
            return ResponseEntity.badRequest().body("As senhas não coincidem.");
        }

        try {
            Long idUsuario = tokenService.verificarToken(dto.getToken());
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new CumbucaException("Usuário não encontrado"));

            usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Senha alterada com sucesso.");
        } catch (CumbucaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}