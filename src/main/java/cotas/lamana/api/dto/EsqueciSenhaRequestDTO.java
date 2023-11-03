package cotas.lamana.api.dto;

public class EsqueciSenhaRequestDTO {
    private String senha;
    private String token;

    // Construtor vazio necessário para a deserialização JSON
    public EsqueciSenhaRequestDTO() {}

    // Construtor com todos os atributos, útil para testes ou criação manual de instâncias
    public EsqueciSenhaRequestDTO(String senha, String token) {
        this.senha = senha;
        this.token = token;
    }

    // Getters e setters
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Métodos utilitários como equals, hashCode e toString podem ser adicionados conforme necessário
}
