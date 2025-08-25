package com.controlfinanceiro.dao.impl;

import com.controlfinanceiro.dao.UsuarioDAO;
import com.controlfinanceiro.model.Usuario;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String INSERT_SQL =
        "INSERT INTO usuario (nome, senha, ativo, data_criacao, data_atualizacao) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE usuario SET nome = ?, senha = ?, ativo = ?, data_atualizacao = ? WHERE id = ?";

    private static final String DELETE_SQL =
        "DELETE FROM usuario WHERE id = ?";

    private static final String SELECT_BY_ID_SQL =
        "SELECT id, nome, senha, ativo, data_criacao, data_atualizacao FROM usuario WHERE id = ?";

    private static final String SELECT_BY_NOME_SQL =
        "SELECT id, nome, senha, ativo, data_criacao, data_atualizacao FROM usuario WHERE nome = ?";

    private static final String SELECT_ALL_SQL =
        "SELECT id, nome, senha, ativo, data_criacao, data_atualizacao FROM usuario ORDER BY nome";

    private static final String SELECT_ATIVOS_SQL =
        "SELECT id, nome, senha, ativo, data_criacao, data_atualizacao FROM usuario WHERE ativo = true ORDER BY nome";

    private static final String VALIDATE_LOGIN_SQL =
        "SELECT COUNT(*) FROM usuario WHERE nome = ? AND senha = ? AND ativo = true";

    @Override
    public Usuario inserir(Usuario usuario) throws DAOException {
        if (usuario == null) {
            throw new DAOException("Usuário inválido para salvar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setBoolean(3, usuario.isAtivo());
            stmt.setTimestamp(4, Timestamp.valueOf(usuario.getDataCriacao()));
            stmt.setTimestamp(5, Timestamp.valueOf(usuario.getDataAtualizacao()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Falha ao salvar usuário");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                }
            }

            return usuario;

        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario atualizar(Usuario usuario) throws DAOException {
        if (usuario == null || usuario.getId() == null) {
            throw new DAOException("Usuário inválido para atualizar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            usuario.setDataAtualizacao(LocalDateTime.now());

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setBoolean(3, usuario.isAtivo());
            stmt.setTimestamp(4, Timestamp.valueOf(usuario.getDataAtualizacao()));
            stmt.setLong(5, usuario.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Usuário não encontrado para atualizar");
            }

            return usuario;

        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(Long id) throws DAOException {
        if (id == null) {
            throw new DAOException("ID do usuário não pode ser nulo");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Usuário não encontrado para excluir");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) throws DAOException {
        if (id == null) {
            return Optional.empty();
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorNome(String nome) throws DAOException {
        if (nome == null || nome.trim().isEmpty()) {
            return Optional.empty();
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NOME_SQL)) {

            stmt.setString(1, nome.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuário por nome: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> listarTodos() throws DAOException {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar usuários: " + e.getMessage(), e);
        }

        return usuarios;
    }

    @Override
    public List<Usuario> listarAtivos() throws DAOException {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ATIVOS_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar usuários ativos: " + e.getMessage(), e);
        }

        return usuarios;
    }

    @Override
    public boolean validarLogin(String nome, String senha) throws DAOException {
        if (nome == null || nome.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(VALIDATE_LOGIN_SQL)) {

            stmt.setString(1, nome.trim());
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new DAOException("Erro ao validar login: " + e.getMessage(), e);
        }
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setAtivo(rs.getBoolean("ativo"));
        usuario.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        usuario.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
        return usuario;
    }
}
