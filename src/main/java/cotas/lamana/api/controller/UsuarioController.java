package cotas.lamana.api.controller;

import cotas.lamana.api.dto.EmailDTO;
import cotas.lamana.api.service.exceptions.TokenExpiradoException;
import cotas.lamana.api.service.usuario.UsuarioService;
import cotas.lamana.api.usuario.DadosCadastroUsuario;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuario_service;

    @Value("${app.domainUrl}")
    private String domainUrl;

    @PostMapping
    @Transactional
    @RequestMapping("/cadastrar")
    public void cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        usuario_service.cadastrarUsuario(dados);

    }

    @GetMapping("/confirmar-email")
    public ResponseEntity<?> confirmarEmail(@RequestParam String token, HttpServletResponse httpServletResponse) {
        try {
            if (usuario_service.validarToken(token)) {
                httpServletResponse.sendRedirect(domainUrl + "/#/confirmar-email");
                return ResponseEntity.ok().build();
            } else {
                httpServletResponse.sendRedirect(domainUrl + "/#/login");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido");
            }
        } catch (TokenExpiradoException e) {
            try {
                httpServletResponse.sendRedirect(domainUrl + "/#/login");
            } catch (IOException ioException) {
                // Tratar erro de redirecionamento aqui
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expirado");
        } catch (IOException e) {
            // Tratar erro de redirecionamento aqui
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao redirecionar");
        }
    }

    @PostMapping("/reenviar-email")
    public ResponseEntity<?> reenviarEmail(@RequestBody String email) {
        try {
            usuario_service.reenviarEmailDeConfirmacao(email);
            return ResponseEntity.ok("E-mail reenviado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao reenviar e-mail");
        }
    }

    @GetMapping("/validar-esqueci-senha")
    public ResponseEntity<String> validarEsqueciSenha(@RequestParam String token, HttpServletResponse httpServletResponse) {
        try {
            if (usuario_service.validarToken(token)) {
                httpServletResponse.sendRedirect(domainUrl + "/#/redefinir-senha?token=" + token );
                return ResponseEntity.ok().build();
            } else {
                httpServletResponse.sendRedirect(domainUrl + "/#/login");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido");
            }
        } catch (TokenExpiradoException e) {
            try {
                httpServletResponse.sendRedirect(domainUrl + "/#/login");
            } catch (IOException ioException) {
                // Tratar erro de redirecionamento aqui
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expirado");
        } catch (IOException e) {
            // Tratar erro de redirecionamento aqui
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao redirecionar");
        }
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<?> esqueciSenha(@RequestBody EmailDTO email) {
        Optional<String> resultado = usuario_service.processarEsqueciSenha(email.getEmail());
        return resultado.<ResponseEntity<?>>map(s -> ResponseEntity.ok(Map.of("message", s))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "E-mail não cadastrado")));
    }


    /*
    @GetMapping
    @RequestMapping("/logar")
    public void realizarLogin(@RequestBody @Valid DadosCadastroUsuario dados) {
        usuario_service.realizarLogin();

    }

     */

}
