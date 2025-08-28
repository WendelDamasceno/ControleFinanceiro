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

    // SQL atualizado para incluir usuario_id
    private static final String INSERT_SQL =
        "INSERT INTO transacao (descricao, valor, data_transacao, tipo, categoria_id, usuario_id, observacoes, ativo, data_criacao, data_atualizacao) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE transacao SET descricao = ?, valor = ?, data_transacao = ?, tipo = ?, categoria_id = ?, usuario_id = ?, observacoes = ?, ativo = ?, data_atualizacao = ? WHERE id = ?";

    private static final String DELETE_SQL =
        "DELETE FROM transacao WHERE id = ?";

    // SQLs compatíveis com a estrutura atual do banco (sem usuario_id)
    private static final String SELECT_BY_ID_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id WHERE t.id = ?";

    private static final String SELECT_ALL_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id WHERE t.ativo = true ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_PERIOD_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.data_transacao BETWEEN ? AND ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_CATEGORIA_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.categoria_id = ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_TIPO_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.tipo = ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_DESCRICAO_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND LOWER(t.descricao) LIKE LOWER(?) ORDER BY t.data_transacao DESC";

    // SQLs para filtrar por usuário
    private static final String SELECT_BY_USUARIO_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.usuario_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.usuario_id = ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_USUARIO_AND_PERIOD_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.usuario_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.usuario_id = ? AND t.data_transacao BETWEEN ? AND ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_BY_USUARIO_AND_TIPO_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.usuario_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.usuario_id = ? AND t.tipo = ? ORDER BY t.data_transacao DESC";

    private static final String SELECT_ULTIMAS_BY_USUARIO_SQL =
        "SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.categoria_id, t.usuario_id, t.observacoes, t.ativo, t.data_criacao, t.data_atualizacao, " +
        "c.nome as categoria_nome, c.descricao as categoria_descricao " +
        "FROM transacao t LEFT JOIN categoria c ON t.categoria_id = c.id " +
        "WHERE t.ativo = true AND t.usuario_id = ? ORDER BY t.data_transacao DESC LIMIT ?";

    private static final String CALCULATE_TOTAL_BY_USUARIO_AND_TIPO_SQL =
        "SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE ativo = true AND usuario_id = ? AND tipo = ?";

    private static final String CALCULATE_TOTAL_BY_USUARIO_AND_PERIOD_SQL =
        "SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE ativo = true AND usuario_id = ? AND data_transacao BETWEEN ? AND ?";

    @Override
    public Transacao inserir(Transacao transacao) throws DAOException {
        if (transacao == null) {
            throw new DAOException("Transação inválida para salvar");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, transacao.getDescricao());
            stmt.setBigDecimal(2, transacao.getValor());
            stmt.setDate(3, Date.valueOf(transacao.getDataTransacao()));
            stmt.setString(4, transacao.getTipo().name());
            stmt.setObject(5, transacao.getCategoriaId());
            stmt.setLong(6, transacao.getUsuarioId());
            stmt.setString(7, transacao.getObservacao());
            stmt.setBoolean(8, transacao.isAtivo());
            stmt.setTimestamp(9, Timestamp.valueOf(transacao.getDataCriacao()));
            stmt.setTimestamp(10, Timestamp.valueOf(transacao.getDataAtualizacao()));

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
        if (transacao == null || transacao.getId() == null) {
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
            stmt.setLong(6, transacao.getUsuarioId());
            stmt.setString(7, transacao.getObservacao());
            stmt.setBoolean(8, transacao.isAtivo());
            stmt.setTimestamp(9, Timestamp.valueOf(transacao.getDataAtualizacao()));
            stmt.setLong(10, transacao.getId());

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
    public void excluir(Long id) throws DAOException {
        if (id == null) {
            throw new DAOException("ID da transação não pode ser nulo");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Transação não encontrada para excluir");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir transação: " + e.getMessage(), e);
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
    public List<Transacao> buscarPorCategoria(Long categoriaId) throws DAOException {
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
    public List<Transacao> buscarPorTipo(TipoTransacao tipo) throws DAOException {
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
    public List<Transacao> listarPorTipo(TipoTransacao tipo) throws DAOException {
        return buscarPorTipo(tipo);
    }

    @Override
    public BigDecimal calcularTotalPorTipo(TipoTransacao tipo) throws DAOException {
        if (tipo == null) {
            throw new DAOException("Tipo da transação não pode ser nulo");
        }

        String sql = "SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE ativo = true AND tipo = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipo.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular total por tipo: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcularTotalPorPeriodo(LocalDate inicio, LocalDate fim) throws DAOException {
        if (inicio == null || fim == null) {
            throw new DAOException("Datas de início e fim não podem ser nulas");
        }

        String sql = "SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE ativo = true AND data_transacao BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular total por período: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcularTotalPorCategoriaEPeriodo(Long categoriaId, LocalDate inicio, LocalDate fim) throws DAOException {
        if (categoriaId == null || inicio == null || fim == null) {
            throw new DAOException("Categoria e datas não podem ser nulas");
        }

        String sql = "SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE ativo = true AND categoria_id = ? AND data_transacao BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, categoriaId);
            stmt.setDate(2, Date.valueOf(inicio));
            stmt.setDate(3, Date.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular total por categoria e período: " + e.getMessage(), e);
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
    public List<Transacao> buscarPorUsuario(Long usuarioId) throws DAOException {
        if (usuarioId == null) {
            throw new DAOException("ID do usuário não pode ser nulo");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USUARIO_SQL)) {

            stmt.setLong(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Transacao> transacoes = new ArrayList<>();
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
                return transacoes;
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar transações por usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transacao> buscarPorUsuarioEPeriodo(Long usuarioId, LocalDate dataInicio, LocalDate dataFim) throws DAOException {
        if (usuarioId == null || dataInicio == null || dataFim == null) {
            throw new DAOException("Usuário e datas de início e fim não podem ser nulos");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USUARIO_AND_PERIOD_SQL)) {

            stmt.setLong(1, usuarioId);
            stmt.setDate(2, Date.valueOf(dataInicio));
            stmt.setDate(3, Date.valueOf(dataFim));

            try (ResultSet rs = stmt.executeQuery()) {
                List<Transacao> transacoes = new ArrayList<>();
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
                return transacoes;
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar transações por usuário e período: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transacao> buscarPorUsuarioETipo(Long usuarioId, TipoTransacao tipo) throws DAOException {
        if (usuarioId == null || tipo == null) {
            throw new DAOException("Usuário e tipo da transação não podem ser nulos");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USUARIO_AND_TIPO_SQL)) {

            stmt.setLong(1, usuarioId);
            stmt.setString(2, tipo.name());

            try (ResultSet rs = stmt.executeQuery()) {
                List<Transacao> transacoes = new ArrayList<>();
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
                return transacoes;
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar transações por usuário e tipo: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calcularTotalPorUsuarioETipo(Long usuarioId, TipoTransacao tipo) throws DAOException {
        if (usuarioId == null || tipo == null) {
            throw new DAOException("Usuário e tipo da transação não podem ser nulos");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CALCULATE_TOTAL_BY_USUARIO_AND_TIPO_SQL)) {

            stmt.setLong(1, usuarioId);
            stmt.setString(2, tipo.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular total por usuário e tipo: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcularTotalPorUsuarioEPeriodo(Long usuarioId, LocalDate inicio, LocalDate fim) throws DAOException {
        if (usuarioId == null || inicio == null || fim == null) {
            throw new DAOException("Usuário e datas não podem ser nulos");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CALCULATE_TOTAL_BY_USUARIO_AND_PERIOD_SQL)) {

            stmt.setLong(1, usuarioId);
            stmt.setDate(2, Date.valueOf(inicio));
            stmt.setDate(3, Date.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao calcular total por usuário e período: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public List<Transacao> buscarUltimasTransacoesPorUsuario(Long usuarioId, int limite) throws DAOException {
        if (usuarioId == null) {
            throw new DAOException("ID do usuário não pode ser nulo");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ULTIMAS_BY_USUARIO_SQL)) {

            stmt.setLong(1, usuarioId);
            stmt.setInt(2, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Transacao> transacoes = new ArrayList<>();
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
                return transacoes;
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar últimas transações por usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transacao> buscarRecentesPorUsuario(Long usuarioId, int limite) throws DAOException {
        String sql = """
            SELECT t.*, 
                   c.nome as categoria_nome, 
                   c.descricao as categoria_descricao
            FROM transacao t
            LEFT JOIN categoria c ON t.categoria_id = c.id
            WHERE t.usuario_id = ? AND t.ativo = true
            ORDER BY t.data_transacao DESC, t.data_criacao DESC
            LIMIT ?
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            stmt.setInt(2, limite);

            ResultSet rs = stmt.executeQuery();
            List<Transacao> transacoes = new ArrayList<>();

            while (rs.next()) {
                transacoes.add(mapResultSetToTransacao(rs));
            }

            return transacoes;

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar transações recentes por usuário", e);
        }
    }

    @Override
    public List<Transacao> buscarPorUsuarioECategoria(Long usuarioId, Long categoriaId) throws DAOException {
        if (usuarioId == null || categoriaId == null) {
            throw new DAOException("ID do usuário e ID da categoria não podem ser nulos");
        }

        String sql = """
            SELECT t.*, 
                   c.nome as categoria_nome, 
                   c.descricao as categoria_descricao
            FROM transacao t
            LEFT JOIN categoria c ON t.categoria_id = c.id
            WHERE t.usuario_id = ? AND t.categoria_id = ? AND t.ativo = true
            ORDER BY t.data_transacao DESC
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            stmt.setLong(2, categoriaId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Transacao> transacoes = new ArrayList<>();
                while (rs.next()) {
                    transacoes.add(mapResultSetToTransacao(rs));
                }
                return transacoes;
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar transações por usuário e categoria: " + e.getMessage(), e);
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

        if (categoriaId != null && rs.getString("categoria_nome") != null) {
            Categoria categoria = new Categoria();
            categoria.setId(categoriaId);
            categoria.setNome(rs.getString("categoria_nome"));
            categoria.setDescricao(rs.getString("categoria_descricao"));
            transacao.setCategoria(categoria);
        }

        // Verificar se a coluna usuario_id existe no ResultSet
        try {
            Long usuarioId = rs.getObject("usuario_id", Long.class);
            transacao.setUsuarioId(usuarioId);
        } catch (SQLException e) {
            // Coluna usuario_id não existe, usar valor padrão
            transacao.setUsuarioId(1L);
        }

        transacao.setObservacao(rs.getString("observacoes"));
        transacao.setAtivo(rs.getBoolean("ativo"));
        transacao.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        transacao.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());

        return transacao;
    }
}
