package repository;

import java.util.HashMap;
import java.util.Map;

import DAO.ClienteDAO;
import DAO.ContaDAO;
import model.Cliente;
import model.Conta;

// Classe de repositório para simular operações CRUD em Cliente e Conta em memória
// **ATENÇÃO: Os dados são perdidos ao finalizar o programa.**
public class ClienteRepository {

    // Armazenamento em memória para clientes, simulando um banco de dados
    private static final Map<Integer, Cliente> clientesMap = new HashMap<>();
    // Gerador de IDs simples para clientes e contas
    private static final ClienteDAO clienteDAO = new ClienteDAO();
    private static final ContaDAO contaDAO = new ContaDAO();

    // Salva um novo cliente na "memória"
    // Retorna o objeto Cliente salvo com seu ID gerado
    public Cliente save(Cliente cliente) {
        // Verifica se o CPF já existe
        if (clientesMap.values().stream().anyMatch(c -> c.getCpf().equals(cliente.getCpf()))) {
            System.err.println("Erro: Já existe um cliente com este CPF.");
            return null;
        }
        // Verifica se o login já existe
        if (clientesMap.values().stream().anyMatch(c -> c.getLogin().equals(cliente.getLogin()))) {
            System.err.println("Erro: Já existe um cliente com este Login.");
            return null;
        }
        // save cliente DB
        Cliente c = clienteDAO.save(cliente);

        return c;
    }

    // Encontra um cliente pelas credenciais de login
    // Retorna o objeto Cliente se encontrado, null caso contrário
    public Cliente findByLoginAndSenha(String login, String senha) {
        // find by login DB
        return clienteDAO.findByLoginAndSenha(login, senha);
    }

    // Encontra um cliente pelo CPF
    // Retorna o objeto Cliente se encontrado, null caso contrário
    public Cliente findByCpf(String cpf) {
        return clienteDAO.findByCpf(cpf);
    }

    // Encontra um cliente pelo número da conta
    // Retorna o objeto Cliente se encontrado, null caso contrário
    public Cliente findByNumeroConta(String numeroConta) {
        return contaDAO.findClientByNumeroConta(numeroConta);
    }

    // Adiciona uma nova conta para um cliente existente
    // Retorna o objeto Conta criada com seu ID gerado
    public Conta addContaToCliente(Cliente cliente, Conta conta) {
        conta.setCliente(cliente); // Garante que a conta está ligada ao cliente
        cliente.adicionarConta(conta); // Adiciona a conta à lista do cliente em memória
        return contaDAO.addContaToCliente(cliente, conta);
    }

    // Atualiza o saldo de uma conta (apenas atualiza o objeto em memória)
    // Retorna true se a conta for encontrada e atualizada, false caso contrário
    public boolean updateContaSaldo(Conta conta) {
        return contaDAO.updateContaSaldo(conta);
    }
}
