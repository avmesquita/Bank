package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Cliente;
import util.ConexaoMySQL;

public class ClienteDAO {

    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, cpf, login, senha) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getLogin());
            stmt.setString(4, cliente.getSenha());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    cliente.setId(id);
                    System.out.println("Cliente salvo no banco com ID: " + id);
                    return cliente;
                }
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.err.println("CPF ou Login já existe no banco.");
            } else {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Cliente findByCpf(String cpf) {
        String sql = "SELECT * FROM clientes WHERE cpf = ?";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setLogin(rs.getString("login"));
                cliente.setSenha(rs.getString("senha"));
                return cliente;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Cliente findByLoginAndSenha(String login, String senha) {
        String sql = "SELECT * FROM clientes WHERE login = ? AND senha = ?";

        try (Connection conn = ConexaoMySQL.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setLogin(rs.getString("login"));
                cliente.setSenha(rs.getString("senha"));
                return cliente;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
