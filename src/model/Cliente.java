package model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Necessário para Objects.equals e Objects.hash

// Representa um cliente no banco digital.
public class Cliente extends Usuario {
    private String nome; // Nome completo do cliente
    private String cpf; // CPF do cliente (identificador único)
    private LocalDate dataCadastro; // Data de registro do cliente
    private List<Conta> contas; // Lista de contas associadas a este cliente

    // Construtor sem argumentos
    public Cliente() {
        super(); // Chama o construtor da superclasse Usuario
        this.contas = new ArrayList<>();
    }

    // Construtor para criar um novo cliente (para o repositório)
    public Cliente(int id, String login, String senha, String nome, String cpf, LocalDate dataCadastro) {
        super(id, login, senha);
        this.nome = nome;
        this.cpf = cpf;
        this.dataCadastro = dataCadastro;
        this.contas = new ArrayList<>();
    }

    // Construtor para criar um novo cliente (sem ID inicial)
    public Cliente(String login, String senha, String nome, String cpf) {
        super(0, login, senha); // ID será definido pelo repositório em memória
        this.nome = nome;
        this.cpf = cpf;
        this.dataCadastro = LocalDate.now(); // Data de cadastro atual
        this.contas = new ArrayList<>(); // Inicializa a lista de contas
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public List<Conta> getContas() {
        return contas;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public void setContas(List<Conta> contas) {
        this.contas = contas;
    }

    // Adiciona uma conta à lista de contas do cliente
    public void adicionarConta(Conta conta) {
        if (this.contas == null) {
            this.contas = new ArrayList<>();
        }
        this.contas.add(conta);
    }

    // Métodos equals e hashCode (gerados manualmente para substituir Lombok)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Compara campos da superclasse
        Cliente cliente = (Cliente) o;
        return Objects.equals(nome, cliente.nome) &&
                Objects.equals(cpf, cliente.cpf) &&
                Objects.equals(dataCadastro, cliente.dataCadastro) &&
                Objects.equals(contas, cliente.contas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nome, cpf, dataCadastro, contas);
    }

    // Método toString (gerado manualmente para substituir Lombok)
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + getId() + // Inclui ID da superclasse
                ", login='" + getLogin() + '\'' + // Inclui login da superclasse
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", contas=" + contas +
                '}';
    }
}
