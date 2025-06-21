package service;

import java.util.HashSet; // Para armazenar números de conta usados
import java.util.Random; // Para gerar números de conta
import java.util.Set; // Para verificar unicidade de números de conta

import DAO.ContaDAO;
import DAO.TransacoesDAO;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import repository.ClienteRepository;

// Camada de serviço para lógica de negócios relacionada ao cliente
public class ClienteService {

    private final ClienteRepository clienteRepository;
    // Para garantir que os números de conta são únicos em memória
    private static final Set<String> numerosContaGerados = new HashSet<>();
    private static final Random random = new Random();

    private static final TransacoesDAO transacoesDAO = new TransacoesDAO();
    // private static final ClienteDAO clienteDAO = new ClienteDAO();
    private static final ContaDAO contaDAO = new ContaDAO();

    // Construtor para injetar a dependência ClienteRepository
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Registra um novo cliente no sistema
    // Retorna o objeto Cliente registrado, ou null se o registro falhar (ex: CPF já
    // existe)
    public Cliente registrarCliente(String nome, String cpf, String login, String senha) {
        // Verifica se CPF já existe antes de tentar salvar
        if (clienteRepository.findByCpf(cpf) != null) {
            System.out.println("Erro: Já existe um cliente com este CPF.");
            return null;
        }
        // Poderia adicionar uma checagem de login aqui também se fosse necessário ser
        // único globalmente
        // (o repositório já faz a checagem ao salvar)

        Cliente novoCliente = new Cliente(login, senha, nome, cpf);
        return clienteRepository.save(novoCliente);
    }

    // Autentica um cliente baseado no login e senha
    // Retorna o objeto Cliente autenticado, ou null se a autenticação falhar
    public Cliente autenticarCliente(String login, String senha) {
        return clienteRepository.findByLoginAndSenha(login, senha);
    }

    // Cria uma nova conta (corrente ou poupança) para um cliente existente
    // Retorna o objeto Conta criada, ou null se a criação falhar
    public Conta criarConta(Cliente cliente, String tipoConta) {
        if (cliente == null) {
            System.out.println("Erro: Cliente inválido para criar conta.");
            return null;
        }

        // Gera um número de conta único
        String numeroConta = generateUniqueAccountNumber();
        if (numeroConta == null) {
            System.out.println("Não foi possível gerar um número de conta único.");
            return null;
        }

        Conta novaConta;

        if ("corrente".equalsIgnoreCase(tipoConta) || "c".equalsIgnoreCase(tipoConta)) {
            novaConta = new ContaCorrente(numeroConta, cliente);
        } else if ("poupanca".equalsIgnoreCase(tipoConta) || "p".equalsIgnoreCase(tipoConta)) {
            novaConta = new ContaPoupanca(numeroConta, cliente);
        } else {
            System.out.println("Tipo de conta inválido. Use 'corrente' ou 'poupanca'.");
            return null;
        }

        // Salva a nova conta (em memória) e a vincula ao cliente
        return clienteRepository.addContaToCliente(cliente, novaConta);
    }

    // Método auxiliar para gerar um número de conta único
    private String generateUniqueAccountNumber() {
        String accountNumber;
        int attempts = 0;
        // Tenta gerar um número único (simples, para evitar colisões em testes)
        do {
            accountNumber = String.format("%06d", random.nextInt(1000000));
            attempts++;
            if (attempts > 100) { // Limita as tentativas para evitar loop infinito em caso de muitos números
                                  // usados
                System.err.println("Limite de tentativas para gerar número de conta único atingido.");
                return null;
            }
        } while (numerosContaGerados.contains(accountNumber)); // Verifica se já foi gerado
        numerosContaGerados.add(accountNumber); // Adiciona ao conjunto de números gerados
        return accountNumber;
    }

    // Realiza uma operação de depósito em uma conta do cliente
    // Retorna true se o depósito for bem-sucedido, false caso contrário
    public boolean depositar(Cliente cliente, String numeroConta, double valor) {

        Conta conta = contaDAO.findContaByNumero(numeroConta);
        conta.depositar(valor);

        transacoesDAO.novaTransacao(conta.getId(), "deposito", valor);

        return clienteRepository.updateContaSaldo(conta); // "Atualiza" o saldo em memória

    }

    // Realiza uma operação de saque de uma conta do cliente
    // Retorna true se o saque for bem-sucedido, false caso contrário
    public boolean sacar(Cliente cliente, String numeroConta, double valor) {
        Conta conta = contaDAO.findContaByNumero(numeroConta);

        if (conta.sacar(valor)) {
            transacoesDAO.novaTransacao(conta.getId(), "saque", valor);
            return clienteRepository.updateContaSaldo(conta); // "Atualiza" o saldo em memória
        }
        return false; // Saque falhou (ex: saldo insuficiente)

    }

    // Realiza uma operação de transferência entre duas contas
    // Retorna true se a transferência for bem-sucedida, false caso contrário
    public boolean transferir(Cliente clienteOrigem, String numeroContaOrigem, String numeroContaDestino,
            double valor) {

        if (contaDAO.findContaByNumero(numeroContaDestino) == null) {
            System.out.println("Conta de origem não encontrada para o cliente logado.");
            return false;
        }

        Conta contaOrigem = contaDAO.findContaByNumero(numeroContaOrigem);

        // Encontra a conta de destino em qualquer cliente do sistema (em memória)
        Cliente clienteDestino = clienteRepository.findByNumeroConta(numeroContaDestino);
        if (clienteDestino == null || !contaDAO.haveContas(clienteDestino)) {
            System.out.println("Conta de destino não encontrada.");
            return false;
        }

        Conta contaDestino = contaDAO.findContaByNumero(numeroContaDestino);

        if (contaDestino.equals(null)) {
            System.out.println("Conta de destino não encontrada (embora o cliente exista).");
            return false;
        }

        if (contaOrigem.transferir(valor, contaDestino)) {
            // Se a lógica de transferência na Conta for bem-sucedida, "atualiza" ambas as
            // contas em memória
            boolean successOrigem = clienteRepository.updateContaSaldo(contaOrigem);
            transacoesDAO.novaTransacao(contaOrigem.getId(), "transferencia", -valor);
            boolean successDestino = clienteRepository.updateContaSaldo(contaDestino);
            transacoesDAO.novaTransacao(contaDestino.getId(), "transferencia", valor);

            if (successOrigem && successDestino) {
                System.out.println("Transferência concluída com sucesso!");
                return true;
            } else {
                System.out.println("Erro ao atualizar saldos em memória após a transferência (estado inconsistente).");
                return false;
            }
        }
        return false; // Transferência falhou (ex: saldo insuficiente)
    }

    // Exibe o extrato para uma conta específica do cliente logado
    public void imprimirExtrato(Cliente cliente, String numeroConta) {
        transacoesDAO.imprimirExtrato(numeroConta);
    }
}
