package com.controlfinanceiro.dao;

import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.enums.TipoTransacao;
import com.controlfinanceiro.exception.DAOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoDAO {

    Transacao salvar(Transacao transacao) throws DAOException;
    Transacao atualizar(Transacao transacao) throws DAOException;
    void deletar(Long id) throws DAOException;
    Optional<Transacao> buscarPorId(Long id) throws DAOException;
    List<Transacao> listarTodas() throws DAOException;
    List<Transacao> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws DAOException;
    List<Transacao> listarPorCategoria(Long categoriaId) throws DAOException;
    List<Transacao> listarPorTipo(TipoTransacao tipo) throws DAOException;
    List<Transacao> listarPorCategoriaEPeriodo(Long categoriaId, LocalDate dataInicio, LocalDate dataFim) throws DAOException;
    BigDecimal calcularTotalReceitas(LocalDate dataInicio, LocalDate dataFim) throws DAOException;
    BigDecimal calcularTotalDespesas(LocalDate dataInicio, LocalDate dataFim) throws DAOException;
    BigDecimal calcularSaldo(LocalDate dataInicio, LocalDate dataFim) throws DAOException;
    BigDecimal calcularTotalPorCategoria(Long categoriaId, LocalDate dataInicio, LocalDate dataFim) throws DAOException;
    List<Transacao> buscarPorDescricao(String descricao) throws DAOException;
    void inativar(Long id) throws DAOException;
}
