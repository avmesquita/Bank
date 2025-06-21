package model;
import java.util.Objects; // Necessário para Objects.equals e Objects.hash

// Representa um gerente no banco digital
public class Gerente extends Usuario {
    private String departamento; // Departamento ao qual o gerente pertence
    private String matricula; // Número de matrícula do gerente

    // Construtor sem argumentos
    public Gerente() {
        super(); // Chama o construtor da superclasse Usuario
    }

    // Construtor com todos os campos (incluindo os herdados de Usuario)
    public Gerente(int id, String login, String senha, String departamento, String matricula) {
        super(id, login, senha);
        this.departamento = departamento;
        this.matricula = matricula;
    }

    // Construtor para criar um novo gerente (sem ID inicial)
    public Gerente(String login, String senha, String departamento, String matricula) {
        super(0, login, senha); // ID será definido pelo repositório em memória
        this.departamento = departamento;
        this.matricula = matricula;
    }

    // Getters
    public String getDepartamento() {
        return departamento;
    }

    public String getMatricula() {
        return matricula;
    }

    // Setters
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    // Métodos equals e hashCode (gerados manualmente para substituir Lombok)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Compara campos da superclasse
        Gerente gerente = (Gerente) o;
        return Objects.equals(departamento, gerente.departamento) &&
                Objects.equals(matricula, gerente.matricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), departamento, matricula);
    }

    // Método toString (gerado manualmente para substituir Lombok)
    @Override
    public String toString() {
        return "Gerente{" +
                "id=" + getId() +
                ", login='" + getLogin() + '\'' +
                ", departamento='" + departamento + '\'' +
                ", matricula='" + matricula + '\'' +
                '}';
    }
}
