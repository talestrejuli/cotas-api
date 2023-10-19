package cotas.lamana.api.service.usuario;

import cotas.lamana.api.service.email.EnviarEmailService;
import cotas.lamana.api.usuario.DadosCadastroUsuario;
import cotas.lamana.api.usuario.Usuario;
import cotas.lamana.api.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private EnviarEmailService email_service;

    public void cadastrarUsuario (@RequestBody @Valid DadosCadastroUsuario dados) {
        repository.save(new Usuario(dados));
        //email_service.sendMail(dados.email(), "talestrejuli@gmail.com", "Confirmação de cadastro", "Teste", false);
    }

}
