package cotas.lamana.api.controller;

import cotas.lamana.api.endereco.Endereco;
import cotas.lamana.api.service.email.EnviarEmailService;
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
    private UsuarioRepository repository;
    @Autowired
    private EnviarEmailService service;

    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {

        repository.save(new Usuario(dados));
        //service.sendMail(dados.email(), "talestrejuli@gmail.com", "Confirmação de cadastro", "Teste", false);
    }

    @GetMapping
    public List<DadosListagemUsuario> listar() {
        return repository.findAll().stream().map(DadosListagemUsuario::new).toList();
    }
}
