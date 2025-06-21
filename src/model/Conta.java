package model;

import java.util.Objects; // Necessário para Objects.equals e Objects.hash

// Classe base abstrata para todas as contas bancárias
public abstract class Conta {
    protected int id; // Identificador único para a conta
    protected String numero; // Número da conta
    protected double saldo; // Saldo atual da conta
    protected Cliente cliente; // O cliente que possui esta conta
    protected String tipo; // Tipo de conta (ex: "Corrente", "Poupanca")

    // Construtor sem argumentos
    public Conta() {
    }

    // Construtor com todos os campos
    public Conta(int id, String numero, double saldo, Cliente cliente, String tipo) {
        this.id = id;
        this.numero = numero;
        this.saldo = saldo;
        this.cliente = cliente;
        this.tipo = tipo;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getTipo() {
        return tipo;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Método abstrato para depositar dinheiro na conta
    public abstract void depositar(double valor);

    // Método abstrato para sacar dinheiro da conta
    // Retorna true se o saque for bem-sucedido, false caso contrário
    public abstract boolean sacar(double valor);

    // Método abstrato para transferir dinheiro desta conta para outra
    // Retorna true se a transferência for bem-sucedida, false caso contrário
    public abstract boolean transferir(double valor, Conta destino);

    // Exibe as informações da conta
    public void imprimirExtrato() {
        System.out.println("--- Extrato da Conta ---");
        System.out.println(String.format("Titular: %s (CPF: %s)", this.cliente.getNome(), this.cliente.getCpf()));
        System.out.println(String.format("Número da Conta: %s", this.numero));
        System.out.println(String.format("Tipo de Conta: %s", this.tipo));
        System.out.println(String.format("Saldo Atual: R$ %.2f", this.saldo));
        System.out.println("------------------------");
    }

    // Métodos equals e hashCode (gerados manualmente para substituir Lombok)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Conta conta = (Conta) o;
        return id == conta.id &&
                Double.compare(conta.saldo, saldo) == 0 &&
                Objects.equals(numero, conta.numero) &&
                Objects.equals(cliente, conta.cliente) &&
                Objects.equals(tipo, conta.tipo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numero, saldo, cliente, tipo);
    }

    // Método toString (gerado manualmente para substituir Lombok)
    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", saldo=" + saldo +
                ", cliente=" + cliente.getNome() + // Para evitar recursão infinita se Cliente.toString chamar
                                                   // Conta.toString
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
