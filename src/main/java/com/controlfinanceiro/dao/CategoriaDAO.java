package com.controlfinanceiro.dao;

import com.controlfinanceiro.model.Categoria;
import com.controlfinanceiro.exception.DAOException;
import java.util.List;
import java.util.Optional;

public interface CategoriaDAO {

    Categoria inserir(Categoria categoria) throws DAOException;
    Categoria atualizar(Categoria categoria) throws DAOException;
    void excluir(Long id) throws DAOException;
    Optional<Categoria> buscarPorId(Long id) throws DAOException;
    List<Categoria> listarTodas() throws DAOException;
    List<Categoria> listarAtivas() throws DAOException;
    List<Categoria> buscarPorNome(String nome) throws DAOException;
    boolean existePorNome(String nome) throws DAOException;
    void inativar(Long id) throws DAOException;
    void ativar(Long id) throws DAOException;
}
