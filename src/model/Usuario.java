package model;
// Classe base para todos os usuários do sistema
public abstract class Usuario {
    protected int id; // Identificador único para o usuário
    protected String login; // Nome de usuário para login
    protected String senha; // Senha do usuário

    // Construtor sem argumentos
    public Usuario() {
    }

    // Construtor com todos os campos
    public Usuario(int id, String login, String senha) {
        this.id = id;
        this.login = login;
        this.senha = senha;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // Método toString
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}
