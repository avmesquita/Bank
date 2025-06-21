import java.util.Scanner;

import DAO.ContaDAO;
import model.Cliente;
import repository.ClienteRepository;
import service.ClienteService;

// Classe principal da aplicação para o sistema de Banco Digital com interface de terminal
public class BancoDigitalApp {

    private static final ContaDAO contaDAO = new ContaDAO();

    private static ClienteService clienteService;
    private static Scanner scanner;
    private static Cliente clienteLogado; // Armazena o cliente atualmente logado

    public static void main(String[] args) {

        scanner = new Scanner(System.in);
        clienteService = new ClienteService(new ClienteRepository());

        System.out.println("Bem-vindo ao Banco Digital (Versão em Memória)! V 2.0");
        System.out.println("Lembre-se: Todos os dados serão perdidos ao fechar o programa.");

        int opcao;
        do {
            mostrarMenuPrincipal();
            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    registrarCliente();
                    break;
                case 2:
                    fazerLogin();
                    break;
                case 0:
                    System.out.println("Saindo... Obrigado por usar o Banco Digital!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println("\nPressione Enter para continuar...");
            scanner.nextLine(); // Consome o newline pendente e espera o Enter
        } while (opcao != 0);

        scanner.close(); // Fecha o scanner ao finalizar
    }

    // Exibe as opções do menu principal
    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1. Registrar Novo Cliente");
        System.out.println("2. Fazer Login");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    // Exibe as opções do menu do cliente logado
    private static void mostrarMenuCliente() {
        System.out.println("\n--- Menu do Cliente ---");
        System.out.println("Cliente Logado: " + clienteLogado.getNome());
        System.out.println("1. Criar Nova Conta");
        System.out.println("2. Depositar");
        System.out.println("3. Sacar");
        System.out.println("4. Transferir");
        System.out.println("5. Ver Extrato de Conta");
        System.out.println("6. Listar Minhas Contas");
        System.out.println("0. Logout");
        System.out.print("Escolha uma opção: ");
    }

    // Lê uma opção inteira do console, lida com entradas inválidas
    private static int lerOpcao() {
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            scanner.next(); // Consome a entrada inválida
            System.out.print("Escolha uma opção: ");
        }
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Consome o caractere de nova linha restante
        return opcao;
    }

    // Lida com o processo de registro de cliente
    private static void registrarCliente() {
        System.out.println("\n--- Registro de Novo Cliente ---");
        System.out.print("Nome Completo: ");
        String nome = scanner.nextLine();
        System.out.print("CPF (apenas números): ");
        String cpf = scanner.nextLine();
        System.out.print("Login desejado: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Cliente novoCliente = clienteService.registrarCliente(nome, cpf, login, senha);

        if (novoCliente != null) {
            System.out.println("Cliente " + novoCliente.getNome() + " registrado com sucesso!");
            System.out.println("Agora você pode fazer login.");
        } else {
            System.out.println("Falha ao registrar cliente. (CPF ou Login já podem existir)");
        }
    }

    // Lida com o processo de login do cliente
    private static void fazerLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        clienteLogado = clienteService.autenticarCliente(login, senha);

        if (clienteLogado != null) {
            System.out.println("Login bem-sucedido! Bem-vindo(a), " + clienteLogado.getNome() + ".");
            menuClienteLogado(); // Entra no menu específico do cliente
        } else {
            System.out.println("Login ou senha inválidos.");
        }
    }

    // Lida com o menu e operações para um cliente logado
    private static void menuClienteLogado() {
        int opcao;
        do {
            mostrarMenuCliente();
            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    criarNovaConta();
                    break;
                case 2:
                    realizarDeposito();
                    break;
                case 3:
                    realizarSaque();
                    break;
                case 4:
                    realizarTransferencia();
                    break;
                case 5:
                    verExtratoConta();
                    break;
                case 6:
                    listarContasCliente();
                    break;
                case 0:
                    System.out.println("Saindo da sua conta. Até mais, " + clienteLogado.getNome() + "!");
                    clienteLogado = null; // Limpa o cliente logado
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            if (opcao != 0) {
                System.out.println("\nPressione Enter para continuar...");
                scanner.nextLine();
            }
        } while (opcao != 0);
    }

    // Cria uma nova conta para o cliente logado
    private static void criarNovaConta() {
        System.out.println("\n--- Criar Nova Conta ---");
        System.out.print("Tipo de conta (corrente/poupanca): ");
        String tipoConta = scanner.nextLine();

        clienteService.criarConta(clienteLogado, tipoConta);
    }

    // Realiza uma operação de depósito
    private static void realizarDeposito() {
        System.out.println("\n--- Realizar Depósito ---");
        if (!contaDAO.haveContas(clienteLogado)) {
            System.out.println("Você não possui contas. Crie uma primeiro.");
            return;
        }
        listarContasCliente();
        System.out.print("Digite o número da conta para depósito: ");
        String numeroConta = scanner.nextLine();
        System.out.print("Digite o valor do depósito: ");
        double valor = lerValorDouble();

        clienteService.depositar(clienteLogado, numeroConta, valor);
    }

    // Realiza uma operação de saque
    private static void realizarSaque() {
        System.out.println("\n--- Realizar Saque ---");
        if (!contaDAO.haveContas(clienteLogado)) {
            System.out.println("Você não possui contas. Crie uma primeiro.");
            return;
        }
        listarContasCliente();
        System.out.print("Digite o número da conta para saque: ");
        String numeroConta = scanner.nextLine();
        System.out.print("Digite o valor do saque: ");
        double valor = lerValorDouble();

        clienteService.sacar(clienteLogado, numeroConta, valor);
    }

    // Realiza uma operação de transferência
    private static void realizarTransferencia() {
        System.out.println("\n--- Realizar Transferência ---");
        if (!contaDAO.haveContas(clienteLogado)) {
            System.out.println("Você não possui contas. Crie uma primeiro.");
            return;
        }
        listarContasCliente();
        System.out.print("Digite o número da sua conta de origem: ");
        String numeroContaOrigem = scanner.nextLine();
        System.out.print("Digite o número da conta de destino: ");
        String numeroContaDestino = scanner.nextLine();
        System.out.print("Digite o valor da transferência: ");
        double valor = lerValorDouble();

        clienteService.transferir(clienteLogado, numeroContaOrigem, numeroContaDestino, valor);
    }

    // Exibe o extrato para uma conta selecionada
    private static void verExtratoConta() {
        System.out.println("\n--- Ver Extrato de Conta ---");
        if (!contaDAO.haveContas(clienteLogado)) {
            System.out.println("Você não possui contas. Crie uma primeiro.");
            return;
        }
        listarContasCliente();
        System.out.print("Digite o número da conta para ver o extrato: ");
        String numeroConta = scanner.nextLine();

        clienteService.imprimirExtrato(clienteLogado, numeroConta);
    }

    // Lista todas as contas do cliente atualmente logado
    private static void listarContasCliente() {
        contaDAO.listarContasCliente(clienteLogado);
    }

    // Método auxiliar para ler um valor double, lida com entradas inválidas
    private static double lerValorDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Entrada inválida. Por favor, digite um número válido (ex: 100.00).");
            scanner.next(); // Consome a entrada inválida
            System.out.print("Digite o valor: ");
        }
        double valor = scanner.nextDouble();
        scanner.nextLine(); // Consome o caractere de nova linha restante
        return valor;
    }
}
