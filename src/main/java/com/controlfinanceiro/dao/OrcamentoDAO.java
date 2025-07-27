package com.controlfinanceiro.dao;

import com.controlfinanceiro.model.Orcamento;
import com.controlfinanceiro.exception.DAOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrcamentoDAO {

    Orcamento salvar(Orcamento orcamento) throws DAOException;
    Orcamento atualizar(Orcamento orcamento) throws DAOException;
    void deletar(Long id) throws DAOException;
    Optional<Orcamento> buscarPorId(Long id) throws DAOException;
    List<Orcamento> listarTodos() throws DAOException;
    List<Orcamento> listarPorCategoria(Long categoriaId) throws DAOException;
    List<Orcamento> listarPorPeriodo(int mes, int ano) throws DAOException;
    Optional<Orcamento> buscarPorCategoriaEPeriodo(Long categoriaId, int mes, int ano) throws DAOException;
    List<Orcamento> listarPorAno(int ano) throws DAOException;
    BigDecimal calcularTotalOrcamentoPeriodo(int mes, int ano) throws DAOException;
    boolean existeOrcamentoPorCategoriaEPeriodo(Long categoriaId, int mes, int ano) throws DAOException;
    void inativar(Long id) throws DAOException;
    void ativar(Long id) throws DAOException;
}
