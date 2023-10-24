package cotas.lamana.api.controller;

import cotas.lamana.api.endereco.Endereco;
import cotas.lamana.api.service.email.EnviarEmailService;
import cotas.lamana.api.service.usuario.UsuarioService;
import cotas.lamana.api.usuario.DadosCadastroUsuario;
import cotas.lamana.api.usuario.DadosListagemUsuario;
import cotas.lamana.api.usuario.Usuario;
import cotas.lamana.api.usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuario_service;

    @PostMapping
    @Transactional
    @RequestMapping("/cadastrar")
    public void cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        usuario_service.cadastrarUsuario(dados);

    }

    @PostMapping
    @Transactional
    @RequestMapping("/enviar-email")
    public void enviarEmail(@RequestBody String emailDestinatario) {
        usuario_service.enviarEmailDeConfirmacao(emailDestinatario);
    }

    /*
    @GetMapping
    @RequestMapping("/logar")
    public void realizarLogin(@RequestBody @Valid DadosCadastroUsuario dados) {
        usuario_service.realizarLogin();

    }

     */

}
