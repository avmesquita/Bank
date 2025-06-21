package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import util.ConexaoMySQL;

public class ContaDAO {

    public Cliente findClientByNumeroConta(String numeroConta) {
        String sql = "SELECT c.* FROM clientes c " +
                "JOIN contas ct ON c.id = ct.cliente_id " +
                "WHERE ct.numero = ?";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setLogin(rs.getString("login"));
                cliente.setSenha(rs.getString("senha"));

                // Opcional: buscar as contas do cliente também, se quiser preencher a lista
                // cliente.setContas(buscarContasDoCliente(cliente.getId()));

                return cliente;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Conta findContaByNumero(String numeroConta) {
        String sql = "SELECT ct.*, c.id as cliente_id, c.nome, c.cpf, c.login, c.senha " +
                "FROM contas ct " +
                "JOIN clientes c ON ct.cliente_id = c.id " +
                "WHERE ct.numero = ?";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setLogin(rs.getString("login"));
                cliente.setSenha(rs.getString("senha"));

                String tipoConta = rs.getString("tipo");
                Conta conta;

                if ("Corrente".equalsIgnoreCase(tipoConta)) {
                    conta = new ContaCorrente();
                } else if ("Poupanca".equalsIgnoreCase(tipoConta)) {
                    conta = new ContaPoupanca();
                } else {
                    System.err.println("Tipo de conta desconhecido: " + tipoConta);
                    return null;
                }

                conta.setId(rs.getInt("id"));
                conta.setNumero(rs.getString("numero"));
                conta.setTipo(tipoConta);
                conta.setSaldo(rs.getDouble("saldo"));
                conta.setCliente(cliente);

                return conta;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Conta addContaToCliente(Cliente cliente, Conta conta) {
        if (cliente == null) {
            System.err.println("Cliente não pode ser nulo para adicionar conta.");
            return null;
        }

        String verificarSql = "SELECT COUNT(*) FROM contas WHERE numero = ?";
        String insertSql = "INSERT INTO contas (numero, tipo, saldo, cliente_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.conectar()) {
            // Verifica se já existe uma conta com esse número
            try (PreparedStatement verificarStmt = conn.prepareStatement(verificarSql)) {
                verificarStmt.setString(1, conta.getNumero());
                ResultSet rs = verificarStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("Erro: Já existe uma conta com este número.");
                    return null;
                }
            }

            // Insere a nova conta
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, conta.getNumero());
                insertStmt.setString(2, conta.getTipo());
                insertStmt.setDouble(3, conta.getSaldo());
                insertStmt.setInt(4, cliente.getId());

                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows == 0) {
                    System.err.println("Erro: Falha ao adicionar conta.");
                    return null;
                }

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newContaId = generatedKeys.getInt(1);
                        conta.setId(newContaId);
                        conta.setCliente(cliente);

                        System.out.println(
                                String.format("Conta %s de número %s adicionada ao cliente %s (ID da conta: %d).",
                                        conta.getTipo(), conta.getNumero(), cliente.getNome(), newContaId));
                        return conta;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateContaSaldo(Conta conta) {
        if (conta == null || conta.getId() <= 0) {
            System.err.println("Conta inválida para atualização.");
            return false;
        }

        String updateSql = "UPDATE contas SET saldo = ? WHERE id = ?";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setDouble(1, conta.getSaldo());
            stmt.setInt(2, conta.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.printf("Saldo da conta %s atualizado no banco para R$ %.2f.\n",
                        conta.getNumero(), conta.getSaldo());
                return true;
            } else {
                System.err.println("Erro: Nenhuma conta foi atualizada. ID pode estar incorreto.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void listarContasCliente(Cliente clienteLogado) {
        System.out.println("\n--- Suas Contas ---");

        String sql = "SELECT * FROM contas WHERE cliente_id = ?";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteLogado.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                boolean encontrou = false;
                int i = 1;
                while (rs.next()) {
                    encontrou = true;
                    String numero = rs.getString("numero");
                    String tipo = rs.getString("tipo");
                    double saldo = rs.getDouble("saldo");

                    System.out.printf("%d. Número: %s | Tipo: %s | Saldo: R$ %.2f\n",
                            i++, numero, tipo, saldo);
                }

                if (!encontrou) {
                    System.out.println("Você não possui nenhuma conta cadastrada.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar contas: " + e.getMessage());
        }

        System.out.println("---------------------");
    }

    public boolean haveContas(Cliente cliente) {
        if (cliente == null || cliente.getId() == 0) {
            System.err.println("Cliente inválido ou sem ID.");
            return false;
        }

        String sql = "SELECT COUNT(*) FROM contas WHERE cliente_id = ?";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cliente.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int quantidade = rs.getInt(1);
                    return quantidade > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar contas no banco: " + e.getMessage());
        }

        return false;
    }

}
