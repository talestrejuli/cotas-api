package cotas.lamana.api.service.usuario;

// Importações necessárias
import cotas.lamana.api.service.email.EnviarEmailService;
import cotas.lamana.api.usuario.DadosCadastroUsuario;
import cotas.lamana.api.usuario.Usuario;
import cotas.lamana.api.usuario.UsuarioRepository;
import cotas.lamana.api.util.TokenGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.core.io.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service  // Indica que esta classe é um serviço Spring
public class UsuarioService {

    @Autowired  // Injeção de dependência
    private UsuarioRepository repository;

    @Autowired  // Injeção de dependência
    private EnviarEmailService email_service;

    @Autowired  // Injeção de dependência para o ResourceLoader
    private ResourceLoader resourceLoader;

    // Método para ler um arquivo HTML e retornar seu conteúdo como uma String
    public String readHtmlFile(String path) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        InputStream inputStream = resource.getInputStream();
        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
        return new String(bdata, StandardCharsets.UTF_8);
    }

    // Método para enviar e-mail de confirmação
    public void enviarEmailDeConfirmacao(String emailDestinatario) {
        try {
            String htmlContent = readHtmlFile("templates/email-template.html");
            email_service.sendMail(emailDestinatario, "talestrejuli@gmail.com", "Confirmação de cadastro", htmlContent, true);
        } catch (IOException e) {
            // Loga o erro para depuração
            System.err.println("Ocorreu um erro ao ler o arquivo HTML ou enviar o e-mail: " + e.getMessage());
        }
    }

    public void saveTokenToDatabase(Long userId, String token) {
        // Busca o usuário pelo ID
        Optional<Usuario> optionalUsuario = repository.findById(userId);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Configura o token e a data de expiração
            usuario.setToken(token);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 24);  // 24 horas de expiração
            Date expiryDate = calendar.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expiryDateString = sdf.format(expiryDate);

            // Atualiza o usuário no banco de dados
            repository.save(usuario);
        } else {
            // Trate o caso em que o usuário não é encontrado, se necessário
        }
    }

    // Método para cadastrar um novo usuário
    public void cadastrarUsuario (@RequestBody @Valid DadosCadastroUsuario dados) {
        // Salva o novo usuário no banco de dados e guarda o retorno em uma variável
        Usuario newUser = repository.save(new Usuario(dados));

        // Gera um token único
        String token = TokenGenerator.generateToken();

        // Salva o token no banco de dados com ID do usuário e a data de expiração
        saveTokenToDatabase(newUser.getId(), token);

        enviarEmailDeConfirmacao(dados.email());  // Chama o método para enviar o e-mail de confirmação
    }

    /*
    // Método para realizar o login (comentado no momento)
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
