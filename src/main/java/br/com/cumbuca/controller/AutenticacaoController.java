package br.com.cumbuca.controller;

import br.com.cumbuca.dto.login.LoginRequestDTO;
import br.com.cumbuca.dto.login.LoginResponseDTO;
import br.com.cumbuca.dto.recuperarSenha.AlterarSenhaRequestDTO;
import br.com.cumbuca.dto.recuperarSenha.RecuperarSenhaRequestDTO;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.service.autenticacao.RecuperarSenhaService;
import br.com.cumbuca.service.autenticacao.TokenService;
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

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService, RecuperarSenhaService recuperarSenhaService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.recuperarSenhaService = recuperarSenhaService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        final UsernamePasswordAuthenticationToken autenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getSenha());
        final Authentication authentication = authenticationManager.authenticate(autenticationToken);
        final Usuario usuario = (Usuario) authentication.getPrincipal();
        final String tokenAcesso = tokenService.gerarToken(usuario);
        return ResponseEntity.ok(new LoginResponseDTO(tokenAcesso, usuario.getId()));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@Valid @RequestBody RecuperarSenhaRequestDTO recuperarSenhaRequestDTO) {
        this.recuperarSenhaService.recuperarSenha(recuperarSenhaRequestDTO.getEmail());
        return ResponseEntity.ok("E-mail enviado.");
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<String> alterarSenha(@RequestBody AlterarSenhaRequestDTO alterarSenhaRequestDTO) {
        recuperarSenhaService.alterarSenha(alterarSenhaRequestDTO.getToken(), alterarSenhaRequestDTO.getNovaSenha(), alterarSenhaRequestDTO.getConfirmarNovaSenha());
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}