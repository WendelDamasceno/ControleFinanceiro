package com.controlfinanceiro.dao;

import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.enums.TipoTransacao;
import com.controlfinanceiro.exception.DAOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoDAO {

    Transacao inserir(Transacao transacao) throws DAOException;
    Transacao atualizar(Transacao transacao) throws DAOException;
    void excluir(Long id) throws DAOException;
    Optional<Transacao> buscarPorId(Long id) throws DAOException;
    List<Transacao> listarTodas() throws DAOException;
    List<Transacao> listarPorPeriodo(LocalDate inicio, LocalDate fim) throws DAOException;
    List<Transacao> buscarPorCategoria(Long categoriaId) throws DAOException;
    List<Transacao> buscarPorTipo(TipoTransacao tipo) throws DAOException;
    List<Transacao> listarPorTipo(TipoTransacao tipo) throws DAOException;
    BigDecimal calcularTotalPorTipo(TipoTransacao tipo) throws DAOException;
    BigDecimal calcularTotalPorPeriodo(LocalDate inicio, LocalDate fim) throws DAOException;
    BigDecimal calcularTotalPorCategoriaEPeriodo(Long categoriaId, LocalDate inicio, LocalDate fim) throws DAOException;
    List<Transacao> buscarPorDescricao(String descricao) throws DAOException;

    // Métodos para filtrar por usuário
    List<Transacao> buscarPorUsuario(Long usuarioId) throws DAOException;
    List<Transacao> buscarPorUsuarioEPeriodo(Long usuarioId, LocalDate dataInicio, LocalDate dataFim) throws DAOException;
    List<Transacao> buscarPorUsuarioETipo(Long usuarioId, TipoTransacao tipo) throws DAOException;
    BigDecimal calcularTotalPorUsuarioETipo(Long usuarioId, TipoTransacao tipo) throws DAOException;
    BigDecimal calcularTotalPorUsuarioEPeriodo(Long usuarioId, LocalDate inicio, LocalDate fim) throws DAOException;
    List<Transacao> buscarUltimasTransacoesPorUsuario(Long usuarioId, int limite) throws DAOException;
}
