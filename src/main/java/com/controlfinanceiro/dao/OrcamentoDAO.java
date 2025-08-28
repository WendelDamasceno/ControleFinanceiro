package com.controlfinanceiro.dao;

import com.controlfinanceiro.model.Orcamento;
import com.controlfinanceiro.exception.DAOException;
import java.util.List;
import java.util.Optional;

public interface OrcamentoDAO {

    Orcamento inserir(Orcamento orcamento) throws DAOException;
    Orcamento atualizar(Orcamento orcamento) throws DAOException;
    void excluir(Long id) throws DAOException;
    Optional<Orcamento> buscarPorId(Long id) throws DAOException;
    List<Orcamento> listarTodos() throws DAOException;
    List<Orcamento> buscarPorPeriodo(int mes, int ano) throws DAOException;
    List<Orcamento> buscarPorCategoria(Long categoriaId) throws DAOException;
    List<Orcamento> buscarPorCategoriaEPeriodo(Long categoriaId, int mes, int ano) throws DAOException;
    List<Orcamento> buscarPorUsuarioEPeriodo(Long usuarioId, int mes, int ano) throws DAOException;
    boolean existeOrcamentoPorCategoriaEPeriodo(Long categoriaId, int mes, int ano) throws DAOException;
}
