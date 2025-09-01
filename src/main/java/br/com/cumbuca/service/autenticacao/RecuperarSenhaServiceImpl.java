package br.com.cumbuca.service.autenticacao;

import br.com.cumbuca.dto.senha.EmailRequestDTO;
import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RecuperarSenhaServiceImpl implements RecuperarSenhaService {

    private final JavaMailSender javaMailSender;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RecuperarSenhaServiceImpl(JavaMailSender javaMailSender, TokenService tokenService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.javaMailSender = javaMailSender;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void recuperarSenha(String email) {
        final Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);

        final String token = tokenService.gerarToken(usuario);
        final String link = "http://cumbuca.onrender.com/alterar-senha?token=" + token;

        final EmailRequestDTO mail = EmailRequestDTO.builder()
                .destinatario(email)
                .assunto("Recuperação de senha do Cumbuca")
                .texto("Clique no link para redefinir sua senha: " + link)
                .build();

        this.enviarEmail(mail);
    }

    @Override
    public void alterarSenha(String token, String novaSenha, String confirmarNovaSenha) {
        if (!novaSenha.equals(confirmarNovaSenha)) {
            throw new CumbucaException("As senhas não coincidem.");
        }

        final Long idUsuario = tokenService.verificarToken(token);
        final Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    private void enviarEmail(EmailRequestDTO mailBody) {
        final SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(mailBody.getDestinatario());
        mensagem.setFrom("cumbuca.software@gmail.com");
        mensagem.setSubject(mailBody.getAssunto());
        mensagem.setText(mailBody.getTexto());

        javaMailSender.send(mensagem);
    }
}