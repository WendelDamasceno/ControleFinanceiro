package com.controlfinanceiro.dao.impl;

import com.controlfinanceiro.dao.OrcamentoDAO;
import com.controlfinanceiro.model.Orcamento;
import com.controlfinanceiro.model.Categoria;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.util.ConnectionFactory;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrcamentoDAOImpl implements OrcamentoDAO {

    private static final String INSERT_SQL =
        "INSERT INTO orcamento (categoria_id, valor_limite, mes, ano, descricao, ativo, data_criacao, data_atualizacao) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE orcamento SET categoria_id = ?, valor_limite = ?, mes = ?, ano = ?, descricao = ?, ativo = ?, data_atualizacao = ? WHERE id = ?";

    private static final String DELETE_SQL =
        "DELETE FROM orcamento WHERE id = ?";

    private static final String SELECT_BY_ID_SQL =
        "SELECT o.id, o.categoria_id, o.valor_limite, o.mes, o.ano, o.descricao, o.ativo, o.data_criacao, o.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM orcamento o LEFT JOIN categoria c ON o.categoria_id = c.id WHERE o.id = ?";

    private static final String SELECT_ALL_SQL =
        "SELECT o.id, o.categoria_id, o.valor_limite, o.mes, o.ano, o.descricao, o.ativo, o.data_criacao, o.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM orcamento o LEFT JOIN categoria c ON o.categoria_id = c.id WHERE o.ativo = true ORDER BY o.ano DESC, o.mes DESC";

    private static final String SELECT_BY_CATEGORIA_SQL =
        "SELECT o.id, o.categoria_id, o.valor_limite, o.mes, o.ano, o.descricao, o.ativo, o.data_criacao, o.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM orcamento o LEFT JOIN categoria c ON o.categoria_id = c.id " +
        "WHERE o.ativo = true AND o.categoria_id = ? ORDER BY o.ano DESC, o.mes DESC";

    private static final String SELECT_BY_PERIOD_SQL =
        "SELECT o.id, o.categoria_id, o.valor_limite, o.mes, o.ano, o.descricao, o.ativo, o.data_criacao, o.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM orcamento o LEFT JOIN categoria c ON o.categoria_id = c.id " +
        "WHERE o.ativo = true AND o.mes = ? AND o.ano = ? ORDER BY c.nome";

    private static final String SELECT_BY_CATEGORIA_PERIOD_SQL =
        "SELECT o.id, o.categoria_id, o.valor_limite, o.mes, o.ano, o.descricao, o.ativo, o.data_criacao, o.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM orcamento o LEFT JOIN categoria c ON o.categoria_id = c.id " +
        "WHERE o.ativo = true AND o.categoria_id = ? AND o.mes = ? AND o.ano = ?";

    private static final String EXISTS_BY_CATEGORIA_PERIOD_SQL =
        "SELECT COUNT(*) FROM orcamento WHERE categoria_id = ? AND mes = ? AND ano = ? AND ativo = true";

    @Override
    public Orcamento inserir(Orcamento orcamento) throws DAOException {
        if (orcamento == null) {
            throw new DAOException("Orçamento inválido para salvar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            orcamento.setDataCriacao(LocalDateTime.now());
            orcamento.setDataAtualizacao(LocalDateTime.now());

            stmt.setLong(1, orcamento.getCategoriaId());
            stmt.setBigDecimal(2, orcamento.getValorLimite());
            stmt.setInt(3, orcamento.getMes());
            stmt.setInt(4, orcamento.getAno());
            stmt.setString(5, orcamento.getDescricao());
            stmt.setBoolean(6, orcamento.isAtivo());
            stmt.setTimestamp(7, Timestamp.valueOf(orcamento.getDataCriacao()));
            stmt.setTimestamp(8, Timestamp.valueOf(orcamento.getDataAtualizacao()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Falha ao salvar orçamento");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orcamento.setId(generatedKeys.getLong(1));
                }
            }

            return orcamento;

        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar orçamento: " + e.getMessage(), e);
        }
    }

    @Override
    public Orcamento atualizar(Orcamento orcamento) throws DAOException {
        if (orcamento == null || orcamento.getId() == null) {
            throw new DAOException("Orçamento inválido para atualizar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            orcamento.setDataAtualizacao(LocalDateTime.now());

            stmt.setLong(1, orcamento.getCategoriaId());
            stmt.setBigDecimal(2, orcamento.getValorLimite());
            stmt.setInt(3, orcamento.getMes());
            stmt.setInt(4, orcamento.getAno());
            stmt.setString(5, orcamento.getDescricao());
            stmt.setBoolean(6, orcamento.isAtivo());
            stmt.setTimestamp(7, Timestamp.valueOf(orcamento.getDataAtualizacao()));
            stmt.setLong(8, orcamento.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Orçamento não encontrado para atualizar");
            }

            return orcamento;

        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar orçamento: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(Long id) throws DAOException {
        if (id == null) {
            throw new DAOException("ID do orçamento não pode ser nulo");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Orçamento não encontrado para excluir");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir orçamento: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Orcamento> buscarPorId(Long id) throws DAOException {
        if (id == null) {
            return Optional.empty();
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrcamento(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar orçamento por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Orcamento> listarTodos() throws DAOException {
        List<Orcamento> orcamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orcamentos.add(mapResultSetToOrcamento(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar orçamentos: " + e.getMessage(), e);
        }

        return orcamentos;
    }

    @Override
    public List<Orcamento> buscarPorPeriodo(int mes, int ano) throws DAOException {
        List<Orcamento> orcamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PERIOD_SQL)) {

            stmt.setInt(1, mes);
            stmt.setInt(2, ano);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orcamentos.add(mapResultSetToOrcamento(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar orçamentos por período: " + e.getMessage(), e);
        }

        return orcamentos;
    }

    @Override
    public List<Orcamento> buscarPorCategoria(Long categoriaId) throws DAOException {
        if (categoriaId == null) {
            return new ArrayList<>();
        }

        List<Orcamento> orcamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CATEGORIA_SQL)) {

            stmt.setLong(1, categoriaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orcamentos.add(mapResultSetToOrcamento(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar orçamentos por categoria: " + e.getMessage(), e);
        }

        return orcamentos;
    }

    @Override
    public List<Orcamento> buscarPorCategoriaEPeriodo(Long categoriaId, int mes, int ano) throws DAOException {
        if (categoriaId == null) {
            return new ArrayList<>();
        }

        List<Orcamento> orcamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CATEGORIA_PERIOD_SQL)) {

            stmt.setLong(1, categoriaId);
            stmt.setInt(2, mes);
            stmt.setInt(3, ano);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orcamentos.add(mapResultSetToOrcamento(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar orçamentos por categoria e período: " + e.getMessage(), e);
        }

        return orcamentos;
    }

    @Override
    public boolean existeOrcamentoPorCategoriaEPeriodo(Long categoriaId, int mes, int ano) throws DAOException {
        if (categoriaId == null) {
            return false;
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_CATEGORIA_PERIOD_SQL)) {

            stmt.setLong(1, categoriaId);
            stmt.setInt(2, mes);
            stmt.setInt(3, ano);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao verificar existência de orçamento: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public List<Orcamento> buscarPorUsuarioEPeriodo(Long usuarioId, int mes, int ano) throws DAOException {
        if (usuarioId == null) {
            throw new DAOException("ID do usuário não pode ser nulo");
        }

        String sql = """
            SELECT o.id, o.categoria_id, o.valor_limite, o.mes, o.ano, o.descricao, o.ativo, o.data_criacao, o.data_atualizacao,
                   c.nome as categoria_nome, c.descricao as categoria_descricao
            FROM orcamento o 
            LEFT JOIN categoria c ON o.categoria_id = c.id
            WHERE o.ativo = true AND o.usuario_id = ? AND o.mes = ? AND o.ano = ?
            ORDER BY c.nome
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            stmt.setInt(2, mes);
            stmt.setInt(3, ano);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Orcamento> orcamentos = new ArrayList<>();
                while (rs.next()) {
                    orcamentos.add(mapResultSetToOrcamento(rs));
                }
                return orcamentos;
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar orçamentos por usuário e período: " + e.getMessage(), e);
        }
    }

    private Orcamento mapResultSetToOrcamento(ResultSet rs) throws SQLException {
        Orcamento orcamento = new Orcamento();
        orcamento.setId(rs.getLong("id"));
        orcamento.setCategoriaId(rs.getLong("categoria_id"));
        orcamento.setValorLimite(rs.getBigDecimal("valor_limite"));
        orcamento.setMes(rs.getInt("mes"));
        orcamento.setAno(rs.getInt("ano"));
        orcamento.setDescricao(rs.getString("descricao"));
        orcamento.setAtivo(rs.getBoolean("ativo"));
        orcamento.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        orcamento.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());

        if (rs.getString("categoria_nome") != null) {
            Categoria categoria = new Categoria();
            categoria.setId(rs.getLong("categoria_id"));
            categoria.setNome(rs.getString("categoria_nome"));
            categoria.setDescricao(rs.getString("categoria_descricao"));
            orcamento.setCategoria(categoria);
        }

        return orcamento;
    }
}
