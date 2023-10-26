package cotas.lamana.api.controller;

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
import java.util.List;

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
                httpServletResponse.sendRedirect(domainUrl + "/confirmar-email");
                return ResponseEntity.ok().build();
            } else {
                httpServletResponse.sendRedirect(domainUrl + "/login");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido");
            }
        } catch (TokenExpiradoException e) {
            try {
                httpServletResponse.sendRedirect(domainUrl + "/login");
            } catch (IOException ioException) {
                // Tratar erro de redirecionamento aqui
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expirado");
        } catch (IOException e) {
            // Tratar erro de redirecionamento aqui
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao redirecionar");
        }
    }


    /*
    @GetMapping
    @RequestMapping("/logar")
    public void realizarLogin(@RequestBody @Valid DadosCadastroUsuario dados) {
        usuario_service.realizarLogin();

    }

     */

}
