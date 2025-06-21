package model;
// Representa uma Conta Corrente
public class ContaCorrente extends Conta {

    // Construtor sem argumentos
    public ContaCorrente() {
        super();
    }

    // Construtor para criar uma nova Conta Corrente
    public ContaCorrente(int id, String numero, double saldo, Cliente cliente) {
        super(id, numero, saldo, cliente, "Corrente"); // Define o tipo de conta como "Corrente"
    }

    // Construtor para criar uma nova Conta Corrente (sem ID, para criação inicial)
    public ContaCorrente(String numero, Cliente cliente) {
        super(0, numero, 0.0, cliente, "Corrente"); // Saldo inicial é 0.0, ID será definido pelo repositório
    }

    @Override
    public void depositar(double valor) {
        if (valor > 0) {
            this.setSaldo(this.getSaldo() + valor); // Usando setter
            System.out.println(String.format("Depósito de R$ %.2f realizado na Conta Corrente %s.", valor, this.getNumero()));
        } else {
            System.out.println("O valor do depósito deve ser positivo.");
        }
    }

    @Override
    public boolean sacar(double valor) {
        if (valor > 0 && this.getSaldo() >= valor) { // Usando getter
            this.setSaldo(this.getSaldo() - valor); // Usando setter
            System.out.println(String.format("Saque de R$ %.2f realizado da Conta Corrente %s.", valor, this.getNumero()));
            return true;
        } else if (valor <= 0) {
            System.out.println("O valor do saque deve ser positivo.");
        } else {
            System.out.println("Saldo insuficiente na Conta Corrente " + this.getNumero() + ".");
        }
        return false;
    }

    @Override
    public boolean transferir(double valor, Conta destino) {
        if (sacar(valor)) { // Tenta sacar da conta corrente
            destino.depositar(valor); // Deposita na conta de destino
            System.out.println(String.format("Transferência de R$ %.2f da Conta Corrente %s para a Conta %s realizada com sucesso.", valor, this.getNumero(), destino.getNumero()));
            return true;
        }
        System.out.println("Falha na transferência da Conta Corrente " + this.getNumero() + ".");
        return false;
    }

    // toString (gerado manualmente, chamando super)
    @Override
    public String toString() {
        return "ContaCorrente{" +
                "id=" + getId() +
                ", numero='" + getNumero() + '\'' +
                ", saldo=" + getSaldo() +
                ", cliente=" + (getCliente() != null ? getCliente().getNome() : "N/A") +
                ", tipo='" + getTipo() + '\'' +
                '}';
    }
}
