package cotas.lamana.api.usuario;

import cotas.lamana.api.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroUsuario(
        @NotBlank
        String nome,
        @NotBlank @Email
        String email,
        @NotBlank
        String dataNascimento,
        @NotBlank
        String telefone,
        @NotNull
        AceitaAviso aceitaAviso,
        @NotNull @Valid
        DadosEndereco endereco) {

}
