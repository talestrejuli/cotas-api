package cotas.lamana.api.usuario;

public record DadosListagemUsuario(String nome, String email, String telefone, AceitaAviso aceitaAviso) {

    public DadosListagemUsuario(Usuario usuario) {
        this(usuario.getNome(), usuario.getEmail(), usuario.getTelefone(), usuario.getAceitaAviso());
    }
}
