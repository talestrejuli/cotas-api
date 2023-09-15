package cotas.lamana.api.controller;

import cotas.lamana.api.endereco.Endereco;
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

    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {

        repository.save(new Usuario(dados));
    }

    @GetMapping
    public List<DadosListagemUsuario> listar() {
        return repository.findAll().stream().map(DadosListagemUsuario::new).toList();
    }
}
