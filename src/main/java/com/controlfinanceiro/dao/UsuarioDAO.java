package com.controlfinanceiro.dao;

import com.controlfinanceiro.model.Usuario;
import com.controlfinanceiro.exception.DAOException;

import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {
    Usuario inserir(Usuario usuario) throws DAOException;
    Usuario atualizar(Usuario usuario) throws DAOException;
    void excluir(Long id) throws DAOException;
    Optional<Usuario> buscarPorId(Long id) throws DAOException;
    Optional<Usuario> buscarPorNome(String nome) throws DAOException;
    List<Usuario> listarTodos() throws DAOException;
    List<Usuario> listarAtivos() throws DAOException;
    boolean validarLogin(String nome, String senha) throws DAOException;
}
