package br.com.cumbuca.controller;

import br.com.cumbuca.dto.login.LoginRequestDTO;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.service.autenticacao.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO login) {
        var autenticationToken = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getSenha());
        var authentication = authenticationManager.authenticate(autenticationToken);
        String tokenAcesso = tokenService.geraToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(tokenAcesso);
    }

}
