package br.com.cumbuca.controller;

import br.com.cumbuca.dto.login.LoginRequestDTO;
import br.com.cumbuca.dto.login.LoginResponseDTO;
import br.com.cumbuca.dto.senha.AlterarSenhaRequestDTO;
import br.com.cumbuca.dto.senha.RecuperarSenhaRequestDTO;
import br.com.cumbuca.enums.Status;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.service.autenticacao.RecuperarSenhaService;
import br.com.cumbuca.service.autenticacao.TokenService;
import br.com.cumbuca.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RecuperarSenhaService recuperarSenhaService;
    private final UsuarioService usuarioService;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService, RecuperarSenhaService recuperarSenhaService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.recuperarSenhaService = recuperarSenhaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        final UsernamePasswordAuthenticationToken autenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getSenha());
        final Authentication authentication = authenticationManager.authenticate(autenticationToken);
        final Usuario usuario = (Usuario) authentication.getPrincipal();
        final String tokenAcesso = tokenService.gerarToken(usuario);
        if (usuario.getStatus().equals(Status.INATIVO.toString())) {
            usuarioService.altualizarStatus(usuario, Status.ATIVO);
        }
        return ResponseEntity.ok(new LoginResponseDTO(tokenAcesso, usuario.getId()));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@Valid @RequestBody RecuperarSenhaRequestDTO recuperarSenhaRequestDTO) {
        recuperarSenhaService.recuperarSenha(recuperarSenhaRequestDTO.getEmail());
        return ResponseEntity.ok("E-mail enviado.");
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<String> alterarSenha(@Valid @RequestBody AlterarSenhaRequestDTO alterarSenhaRequestDTO) {
        recuperarSenhaService.alterarSenha(alterarSenhaRequestDTO.getToken(), alterarSenhaRequestDTO.getNovaSenha(), alterarSenhaRequestDTO.getConfirmarNovaSenha());
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}