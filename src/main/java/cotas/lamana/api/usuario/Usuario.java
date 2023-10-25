package cotas.lamana.api.usuario;

import cotas.lamana.api.endereco.Endereco;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String dataNascimento;
    private String telefone;
    private String email_confirmado;
    private String token;
    private String data_expiracao;

    @Enumerated(EnumType.STRING)
    @Column(name = "aceita_aviso")
    private AceitaAviso aceitaAviso;

    @Embedded
    private Endereco endereco;
    public Usuario(DadosCadastroUsuario dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.senha = dados.senha();
        this.dataNascimento = dados.dataNascimento();
        this.telefone = dados.telefone();
        this.aceitaAviso = dados.aceitaAviso();
        this.endereco = new Endereco(dados.endereco());
        this.email_confirmado = dados.email_confirmado();
    }
}
