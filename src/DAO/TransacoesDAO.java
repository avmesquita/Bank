package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import util.ConexaoMySQL;

public class TransacoesDAO {

    public void imprimirExtrato(String numeroConta) {
        String contaQuery = "SELECT id FROM contas WHERE numero = ?";
        String transacoesQuery = "SELECT tipo, valor, data FROM transacoes WHERE conta_id = ? ORDER BY data";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement contaStmt = conn.prepareStatement(contaQuery)) {

            contaStmt.setString(1, numeroConta);
            ResultSet contaRs = contaStmt.executeQuery();

            if (!contaRs.next()) {
                System.out.println("Conta não encontrada.");
                return;
            }

            int contaId = contaRs.getInt("id");

            try (PreparedStatement transacoesStmt = conn.prepareStatement(transacoesQuery)) {
                transacoesStmt.setInt(1, contaId);
                ResultSet transacoesRs = transacoesStmt.executeQuery();

                System.out.println("\n--- Extrato da Conta " + numeroConta + " ---");

                boolean encontrou = false;
                while (transacoesRs.next()) {
                    encontrou = true;
                    String tipo = transacoesRs.getString("tipo");
                    double valor = transacoesRs.getDouble("valor");
                    Timestamp data = transacoesRs.getTimestamp("data");

                    System.out.printf("[%s] %s: R$ %.2f\n", data.toLocalDateTime(), tipo.toUpperCase(), valor);
                }

                if (!encontrou) {
                    System.out.println("Nenhuma transação encontrada.");
                }

                System.out.println("------------------------------------");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao imprimir extrato: " + e.getMessage());
        }
    }

    public boolean novaTransacao(int contaId, String tipo, double valor) {
        String sql = "INSERT INTO transacoes (conta_id, tipo, valor, data) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contaId);
            stmt.setString(2, tipo);
            stmt.setDouble(3, valor);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao registrar transação: " + e.getMessage());
            return false;
        }
    }

}
