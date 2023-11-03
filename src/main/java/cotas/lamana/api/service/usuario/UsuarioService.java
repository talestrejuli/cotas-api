package cotas.lamana.api.service.usuario;

// Importações necessárias
import cotas.lamana.api.service.email.EnviarEmailService;
import cotas.lamana.api.service.exceptions.EmailNaoEncontradoException;
import cotas.lamana.api.service.exceptions.TokenExpiradoException;
import cotas.lamana.api.usuario.DadosCadastroUsuario;
import cotas.lamana.api.usuario.Usuario;
import cotas.lamana.api.usuario.UsuarioRepository;
import cotas.lamana.api.util.TokenGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.core.io.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
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

    @Value("${app.domainUrl}")
    private String domainUrl;

    @Value("${app.domaiApinUrl}")
    private String domaiApinUrl;

    @Value("${app.emailRemetente}")
    private String emailRemetente;

    // Método para ler um arquivo HTML e retornar seu conteúdo como uma String
    public String readHtmlFile(String path) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        InputStream inputStream = resource.getInputStream();
        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
        return new String(bdata, StandardCharsets.UTF_8);
    }

    // Método para enviar e-mail de confirmação
    public void enviarEmailDeConfirmacao(String emailDestinatario, String token) {
        try {
            String htmlContent = readHtmlFile("templates/email-template.html");

            String confirmacaoUrl = domaiApinUrl + "/usuarios/confirmar-email?token=" + token;
            htmlContent = htmlContent.replace("{confirmacaoUrl}", confirmacaoUrl);

            email_service.sendMail(emailDestinatario, emailRemetente, "[LAMANA] Confirmação de cadastro", htmlContent, true);
        } catch (IOException e) {
            // Loga o erro para depuração
            System.err.println("Ocorreu um erro ao ler o arquivo HTML ou enviar o e-mail: " + e.getMessage());
        }
    }

    // Método para cadastrar um novo usuário
    public void cadastrarUsuario (@RequestBody @Valid DadosCadastroUsuario dados) {
        Usuario novoDados = repository.save(new Usuario(dados));  // Salva o novo usuário

        //criptografa a senha
        BCryptPasswordEncoder criptografar = new BCryptPasswordEncoder();
        String senhaCriptografada = criptografar.encode(novoDados.getSenha());
        novoDados.setSenha(senhaCriptografada);

        // Gera um token único
        String token = TokenGenerator.generateToken();
        String confirmacaoUrl = domainUrl + "/confirmar-email?token=" + token;

        // Define a data de expiração
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);  // 24 horas de expiração
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expiryDateString = sdf.format(calendar.getTime());

        // Atualiza o novo usuário com o token e a data de expiração
        novoDados.setToken(token);
        novoDados.setData_expiracao(expiryDateString);

        // Salva o usuário atualizado no banco de dados
        repository.save(novoDados);

        enviarEmailDeConfirmacao(dados.email(), token);  // Envia o e-mail de confirmação
    }

    public boolean validarToken(String token) throws TokenExpiradoException {
        Optional<Usuario> optionalUsuario = repository.findByToken(token);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date expiryDate = sdf.parse(usuario.getData_expiracao());
                if (new Date().before(expiryDate)) {
                    usuario.setEmail_confirmado("S");
                    repository.save(usuario);
                    return true;
                } else {
                    throw new TokenExpiradoException("Token expirado");
                }
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }

    //Método para reenviar e-mail de confirmação
    public void reenviarEmailDeConfirmacao(String email) {
        Optional<Usuario> optionalUsuario = repository.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Gera um novo token único
            String novoToken = TokenGenerator.generateToken();

            // Define a data de expiração
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 24);  // 24 horas de expiração
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expiryDateString = sdf.format(calendar.getTime());

            // Atualiza o novo usuário com o token e a data de expiração
            usuario.setToken(novoToken);
            usuario.setData_expiracao(expiryDateString);

            // Salva o usuário atualizado no banco de dados
            repository.save(usuario);

            // Reenvia o e-mail de confirmação
            enviarEmailDeConfirmacao(email, novoToken);
        } else {
            //tratamento de erro
        }
    }

    public Optional<String> processarEsqueciSenha(String email) {
        // Verifica se o e-mail está cadastrado no banco de dados
        Optional<Usuario> optionalUsuario = repository.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Gera um token único para redefinição de senha
            String resetToken = TokenGenerator.generateToken();

            // Define uma nova data de expiração do token
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 24);  // 24 horas de expiração
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expiryDateString = sdf.format(calendar.getTime());

            // Atualiza o novo usuário com o token e a data de expiração
            usuario.setToken(resetToken);
            usuario.setData_expiracao(expiryDateString);

            // Salva o token e data de expiração atualizada no banco de dados
            repository.save(usuario);

            // Envia o e-mail de redefinição de senha
            enviarEmailDeRedefinicaoSenha(email, resetToken);

            return Optional.of("E-mail enviado com sucesso");

        } else {
            return Optional.empty();
        }

    }

    public void enviarEmailDeRedefinicaoSenha(String emailDestinatario, String token) {
        try {
            String htmlContent = readHtmlFile("templates/reset-password-email-template.html");

            String confirmacaoUrl = domaiApinUrl + "/usuarios/validar-esqueci-senha?token=" + token;
            htmlContent = htmlContent.replace("{confirmacaoUrl}", confirmacaoUrl);

            email_service.sendMail(emailDestinatario, emailRemetente, "[LAMANA] Redefinição de senha", htmlContent, true);
        } catch (IOException e) {
            // Loga o erro para depuração
            System.err.println("Ocorreu um erro ao ler o arquivo HTML ou enviar o e-mail: " + e.getMessage());
        }
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
