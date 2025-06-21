package model;
// Representa uma Conta Poupança
public class ContaPoupanca extends Conta {

    // Construtor sem argumentos
    public ContaPoupanca() {
        super();
    }

    // Construtor para criar uma nova Conta Poupança
    public ContaPoupanca(int id, String numero, double saldo, Cliente cliente) {
        super(id, numero, saldo, cliente, "Poupanca"); // Define o tipo de conta como "Poupanca"
    }

    // Construtor para criar uma nova Conta Poupança (sem ID, para criação inicial)
    public ContaPoupanca(String numero, Cliente cliente) {
        super(0, numero, 0.0, cliente, "Poupanca"); // Saldo inicial é 0.0, ID será definido pelo repositório
    }

    @Override
    public void depositar(double valor) {
        if (valor > 0) {
            this.setSaldo(this.getSaldo() + valor); // Usando setter
            System.out.println(String.format("Depósito de R$ %.2f realizado na Conta Poupança %s.", valor, this.getNumero()));
        } else {
            System.out.println("O valor do depósito deve ser positivo.");
        }
    }

    @Override
    public boolean sacar(double valor) {
        if (valor > 0 && this.getSaldo() >= valor) { // Usando getter
            this.setSaldo(this.getSaldo() - valor); // Usando setter
            System.out.println(String.format("Saque de R$ %.2f realizado da Conta Poupança %s.", valor, this.getNumero()));
            return true;
        } else if (valor <= 0) {
            System.out.println("O valor do saque deve ser positivo.");
        } else {
            System.out.println("Saldo insuficiente na Conta Poupança " + this.getNumero() + ".");
        }
        return false;
    }

    @Override
    public boolean transferir(double valor, Conta destino) {
        if (sacar(valor)) { // Tenta sacar da conta poupança
            destino.depositar(valor); // Deposita na conta de destino
            System.out.println(String.format("Transferência de R$ %.2f da Conta Poupança %s para a Conta %s realizada com sucesso.", valor, this.getNumero(), destino.getNumero()));
            return true;
        }
        System.out.println("Falha na transferência da Conta Poupança " + this.getNumero() + ".");
        return false;
    }

    // toString (gerado manualmente, chamando super)
    @Override
    public String toString() {
        return "ContaPoupanca{" +
                "id=" + getId() +
                ", numero='" + getNumero() + '\'' +
                ", saldo=" + getSaldo() +
                ", cliente=" + (getCliente() != null ? getCliente().getNome() : "N/A") +
                ", tipo='" + getTipo() + '\'' +
                '}';
    }
}
