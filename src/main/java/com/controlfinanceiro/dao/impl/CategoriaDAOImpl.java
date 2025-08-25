package com.controlfinanceiro.dao.impl;

import com.controlfinanceiro.dao.CategoriaDAO;
import com.controlfinanceiro.model.Categoria;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDAOImpl implements CategoriaDAO {

    private static final String INSERT_SQL =
        "INSERT INTO categoria (nome, descricao, ativo, data_criacao, data_atualizacao) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE categoria SET nome = ?, descricao = ?, ativo = ?, data_atualizacao = ? WHERE id = ?";

    private static final String DELETE_SQL =
        "DELETE FROM categoria WHERE id = ?";

    private static final String SELECT_BY_ID_SQL =
        "SELECT id, nome, descricao, ativo, data_criacao, data_atualizacao FROM categoria WHERE id = ?";

    private static final String SELECT_ALL_SQL =
        "SELECT id, nome, descricao, ativo, data_criacao, data_atualizacao FROM categoria ORDER BY nome";

    private static final String SELECT_ACTIVE_SQL =
        "SELECT id, nome, descricao, ativo, data_criacao, data_atualizacao FROM categoria WHERE ativo = true ORDER BY nome";

    private static final String SELECT_BY_NAME_SQL =
        "SELECT id, nome, descricao, ativo, data_criacao, data_atualizacao FROM categoria WHERE LOWER(nome) LIKE LOWER(?) AND ativo = true";

    private static final String EXISTS_BY_NAME_SQL =
        "SELECT COUNT(*) FROM categoria WHERE LOWER(nome) = LOWER(?) AND ativo = true";

    @Override
    public Categoria inserir(Categoria categoria) throws DAOException {
        if (categoria == null) {
            throw new DAOException("Categoria inválida para salvar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            categoria.setDataCriacao(LocalDateTime.now());
            categoria.setDataAtualizacao(LocalDateTime.now());

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setBoolean(3, categoria.isAtivo());
            stmt.setTimestamp(4, Timestamp.valueOf(categoria.getDataCriacao()));
            stmt.setTimestamp(5, Timestamp.valueOf(categoria.getDataAtualizacao()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Falha ao salvar categoria");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getLong(1));
                }
            }

            return categoria;

        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public Categoria atualizar(Categoria categoria) throws DAOException {
        if (categoria == null || categoria.getId() == null) {
            throw new DAOException("Categoria inválida para atualizar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            categoria.setDataAtualizacao(LocalDateTime.now());

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setBoolean(3, categoria.isAtivo());
            stmt.setTimestamp(4, Timestamp.valueOf(categoria.getDataAtualizacao()));
            stmt.setLong(5, categoria.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Categoria não encontrada para atualizar");
            }

            return categoria;

        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(Long id) throws DAOException {
        if (id == null) {
            throw new DAOException("ID da categoria não pode ser nulo");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Categoria não encontrada para excluir");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) throws DAOException {
        if (id == null) {
            return Optional.empty();
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategoria(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar categoria por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Categoria> listarTodas() throws DAOException {
        List<Categoria> categorias = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar categorias: " + e.getMessage(), e);
        }

        return categorias;
    }

    @Override
    public List<Categoria> listarAtivas() throws DAOException {
        List<Categoria> categorias = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACTIVE_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar categorias ativas: " + e.getMessage(), e);
        }

        return categorias;
    }

    @Override
    public List<Categoria> buscarPorNome(String nome) throws DAOException {
        if (nome == null || nome.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Categoria> categorias = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NAME_SQL)) {

            stmt.setString(1, "%" + nome.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar categorias por nome: " + e.getMessage(), e);
        }

        return categorias;
    }

    @Override
    public boolean existePorNome(String nome) throws DAOException {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_NAME_SQL)) {

            stmt.setString(1, nome.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao verificar existência da categoria: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public void inativar(Long id) throws DAOException {
        if (id == null) {
            throw new DAOException("ID da categoria não pode ser nulo");
        }

        String sql = "UPDATE categoria SET ativo = false, data_atualizacao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Categoria não encontrada para inativar");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao inativar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public void ativar(Long id) throws DAOException {
        if (id == null) {
            throw new DAOException("ID da categoria não pode ser nulo");
        }

        String sql = "UPDATE categoria SET ativo = true, data_atualizacao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Categoria não encontrada para ativar");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao ativar categoria: " + e.getMessage(), e);
        }
    }

    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setDescricao(rs.getString("descricao"));
        categoria.setAtivo(rs.getBoolean("ativo"));
        categoria.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        categoria.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
        return categoria;
    }
}
