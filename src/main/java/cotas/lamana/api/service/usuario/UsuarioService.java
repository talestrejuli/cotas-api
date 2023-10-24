package cotas.lamana.api.service.usuario;

import cotas.lamana.api.service.email.EnviarEmailService;
import cotas.lamana.api.usuario.DadosCadastroUsuario;
import cotas.lamana.api.usuario.Usuario;
import cotas.lamana.api.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private EnviarEmailService email_service;

    public void cadastrarUsuario (@RequestBody @Valid DadosCadastroUsuario dados) {
        repository.save(new Usuario(dados));
        email_service.sendMail(dados.email(), "talestrejuli@gmail.com", "Confirmação de cadastro", "Teste", false);
    }
/*
    public ResponseEntity<?> realizarLogin (String email, String senha) {
        Optional<Usuario> optionalUsuario = repository.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                // A senha está correta
                // Aqui você pode criar e retornar um token JWT, por exemplo.
                return ResponseEntity.ok("Login realizado com sucesso");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos");
    }

 */
}