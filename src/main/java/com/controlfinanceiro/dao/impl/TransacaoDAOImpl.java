package com.controlfinanceiro.dao.impl;

import com.controlfinanceiro.dao.TransacaoDAO;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.Categoria;
import com.controlfinanceiro.model.enums.TipoTransacao;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.util.ConnectionFactory;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransacaoDAOImpl implements TransacaoDAO {

    private static final String INSERT_SQL =
        "INSERT INTO transacao (descricao, valor, data_transacao, tipo, categoria_id, observacao, ativo, data_criacao, data_atualizacao) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE transacao SET descricao = ?, valor = ?, data_transacao = ?, tipo = ?, categoria_id = ?, observacao = ?, ativo = ?, data_atualizacao = ? WHERE id = ?";

    private static final String DELETE_SQL =
        "DELETE FROM transacao WHERE id = ?";

    private static final String SELECT_BY_ID_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacao, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id WHERE t.id = ?";

    private static final String SELECT_ALL_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacao, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id WHERE t.ativo = true ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_PERIOD_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacao, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.data_transacao BETWEEN ? AND ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_CATEGORIA_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacao, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.categoria_id = ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_TIPO_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacao, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.tipo = ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_CATEGORIA_PERIOD_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacao, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.categoria_id = ? AND t.data_transacao BETWEEN ? AND ? ORDER BY t.data_transacao DESC";

    private static final String SUM_RECEITAS_SQL =
        "SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE ativo = true AND tipo = 'RECEITA' AND data_transacao BETWEEN ? AND ?";

    private static final String SUM_DESPESAS_SQL =
        "SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE ativo = true AND tipo = 'DESPESA' AND data_transacao BETWEEN ? AND ?";

    private static final String SUM_BY_CATEGORIA_SQL =
        "SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE ativo = true AND categoria_id = ? AND data_transacao BETWEEN ? AND ?";

    private static final String SELECT_BY_DESCRICAO_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacao, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND LOWER(t.descricao) LIKE LOWER(?) ORDER BY t.data_transacao DESC";

    private static final String INACTIVATE_SQL =
        "UPDATE transacao SET ativo = false, data_atualizacao = ? WHERE id = ?";

    @Override
    public Transacao salvar(Transacao transacao) throws DAOException {
        if (transacao == null || !transacao.isValid()) {
            throw new DAOException("Transação inválida para salvar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, transacao.getDescricao());
            stmt.setBigDecimal(2, transacao.getValor());
            stmt.setDate(3, Date.valueOf(transacao.getDataTransacao()));
            stmt.setString(4, transacao.getTipo().name());
            stmt.setObject(5, transacao.getCategoriaId());
            stmt.setString(6, transacao.getObservacao());
            stmt.setBoolean(7, transacao.isAtivo());
            stmt.setTimestamp(8, Timestamp.valueOf(transacao.getDataCriacao()));
            stmt.setTimestamp(9, Timestamp.valueOf(transacao.getDataAtualizacao()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Falha ao salvar transação");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transacao.setId(generatedKeys.getLong(1));
                }
            }

            return transacao;

        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar transação: " + e.getMessage(), e);
        }
    }

    @Override
    public Transacao atualizar(Transacao transacao) throws DAOException {
        if (transacao == null || transacao.getId() == null || !transacao.isValid()) {
            throw new DAOException("Transação inválida para atualizar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            transacao.setDataAtualizacao(LocalDateTime.now());

            stmt.setString(1, transacao.getDescricao());
            stmt.setBigDecimal(2, transacao.getValor());
            stmt.setDate(3, Date.valueOf(transacao.getDataTransacao()));
            stmt.setString(4, transacao.getTipo().name());
            stmt.setObject(5, transacao.getCategoriaId());
            stmt.setString(6, transacao.getObservacao());
            stmt.setBoolean(7, transacao.isAtivo());
            stmt.setTimestamp(8, Timestamp.valueOf(transacao.getDataAtualizacao()));
            stmt.setLong(9, transacao.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Transação não encontrada para atualizar");
            }

            return transacao;

        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar transação: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(Long id) throws DAOException {
        if (id == null) {
            throw new DAOException("ID da transação não pode ser nulo");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Transação não encontrada para deletar");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao deletar transação: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Transacao> buscarPorId(Long id) throws DAOException {
        if (id == null) {
            return Optional.empty();
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransacao(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar transação por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transacao> listarTodas() throws DAOException {
        List<Transacao> transacoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                transacoes.add(mapResultSetToTransacao(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar transações: " + e.getMessage(), e);
        }

        return transacoes;
    }

    @Override
    public List<Transacao> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws DAOException {
        if (dataInicio == null || dataFim == null) {
            throw new DAOException("Datas de início e fim não podem ser nulas");
        }

        List<Transacao> transacoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PERIOD_SQL)) {

            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar transações por período: " + e.getMessage(), e);
        }

        return transacoes;
    }

    @Override
    public List<Transacao> listarPorCategoria(Long categoriaId) throws DAOException {
        if (categoriaId == null) {
            throw new DAOException("ID da categoria não pode ser nulo");
        }

        List<Transacao> transacoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CATEGORIA_SQL)) {

            stmt.setLong(1, categoriaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar transações por categoria: " + e.getMessage(), e);
        }

        return transacoes;
    }

    @Override
    public List<Transacao> listarPorTipo(TipoTransacao tipo) throws DAOException {
        if (tipo == null) {
            throw new DAOException("Tipo da transação não pode ser nulo");
        }

        List<Transacao> transacoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TIPO_SQL)) {

            stmt.setString(1, tipo.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar transações por tipo: " + e.getMessage(), e);
        }

        return transacoes;
    }

    @Override
    public List<Transacao> listarPorCategoriaEPeriodo(Long categoriaId, LocalDate dataInicio, LocalDate dataFim) throws DAOException {
        if (categoriaId == null || dataInicio == null || dataFim == null) {
            throw new DAOException("Categoria e datas não podem ser nulas");
        }

        List<Transacao> transacoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CATEGORIA_PERIOD_SQL)) {

            stmt.setLong(1, categoriaId);
            stmt.setDate(2, Date.valueOf(dataInicio));
            stmt.setDate(3, Date.valueOf(dataFim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar transações por categoria e período: " + e.getMessage(), e);
        }

        return transacoes;
    }

    @Override
    public BigDecimal calcularTotalReceitas(LocalDate dataInicio, LocalDate dataFim) throws DAOException {
        if (dataInicio == null || dataFim == null) {
            throw new DAOException("Datas de início e fim não podem ser nulas");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SUM_RECEITAS_SQL)) {

            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular total de receitas: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcularTotalDespesas(LocalDate dataInicio, LocalDate dataFim) throws DAOException {
        if (dataInicio == null || dataFim == null) {
            throw new DAOException("Datas de início e fim não podem ser nulas");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SUM_DESPESAS_SQL)) {

            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular total de despesas: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcularSaldo(LocalDate dataInicio, LocalDate dataFim) throws DAOException {
        BigDecimal receitas = calcularTotalReceitas(dataInicio, dataFim);
        BigDecimal despesas = calcularTotalDespesas(dataInicio, dataFim);
        return receitas.subtract(despesas);
    }

    @Override
    public BigDecimal calcularTotalPorCategoria(Long categoriaId, LocalDate dataInicio, LocalDate dataFim) throws DAOException {
        if (categoriaId == null || dataInicio == null || dataFim == null) {
            throw new DAOException("Categoria e datas não podem ser nulas");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SUM_BY_CATEGORIA_SQL)) {

            stmt.setLong(1, categoriaId);
            stmt.setDate(2, Date.valueOf(dataInicio));
            stmt.setDate(3, Date.valueOf(dataFim));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular total por categoria: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public List<Transacao> buscarPorDescricao(String descricao) throws DAOException {
        if (descricao == null || descricao.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Transacao> transacoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DESCRICAO_SQL)) {

            stmt.setString(1, "%" + descricao.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar transações por descrição: " + e.getMessage(), e);
        }

        return transacoes;
    }

    @Override
    public void inativar(Long id) throws DAOException {
        if (id == null) {
            throw new DAOException("ID da transação não pode ser nulo");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INACTIVATE_SQL)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Transação não encontrada para inativar");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao inativar transação: " + e.getMessage(), e);
        }
    }

    private Transacao mapResultSetToTransacao(ResultSet rs) throws SQLException {
        Transacao transacao = new Transacao();
        transacao.setId(rs.getLong("id"));
        transacao.setDescricao(rs.getString("descricao"));
        transacao.setValor(rs.getBigDecimal("valor"));
        transacao.setDataTransacao(rs.getDate("data_transacao").toLocalDate());
        transacao.setTipo(TipoTransacao.valueOf(rs.getString("tipo")));

        Long categoriaId = rs.getObject("categoria_id", Long.class);
        transacao.setCategoriaId(categoriaId);

        // Mapear categoria se existir
        if (categoriaId != null && rs.getString("categoria_nome") != null) {
            Categoria categoria = new Categoria();
            categoria.setId(categoriaId);
            categoria.setNome(rs.getString("categoria_nome"));
            categoria.setDescricao(rs.getString("categoria_descricao"));
            transacao.setCategoria(categoria);
        }

        transacao.setObservacao(rs.getString("observacao"));
        transacao.setAtivo(rs.getBoolean("ativo"));
        transacao.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        transacao.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());

        return transacao;
    }
}
