package com.controlfinanceiro.controller;

import com.controlfinanceiro.dao.UsuarioDAO;
import com.controlfinanceiro.dao.impl.UsuarioDAOImpl;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.exception.BusinessException;
import com.controlfinanceiro.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UsuarioController {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public void cadastrarUsuario(Usuario usuario) throws BusinessException {
        logger.info("Cadastrando usuário: {}", usuario.getNome());
        try {
            validarUsuario(usuario);

            // Verificar se já existe usuário com o mesmo nome
            Optional<Usuario> usuarioExistente = usuarioDAO.buscarPorNome(usuario.getNome());
            if (usuarioExistente.isPresent()) {
                throw new BusinessException("Já existe um usuário com este nome: " + usuario.getNome());
            }

            usuarioDAO.inserir(usuario);
            logger.info("Usuário cadastrado com sucesso - ID: {}", usuario.getId());
        } catch (DAOException e) {
            logger.error("Erro ao cadastrar usuário", e);
            throw new BusinessException("Erro ao cadastrar usuário: " + e.getMessage(), e);
        }
    }

    public boolean validarLogin(String nome, String senha) throws BusinessException {
        logger.info("Validando login para usuário: {}", nome);
        try {
            if (nome == null || nome.trim().isEmpty()) {
                throw new BusinessException("Nome não pode ser vazio");
            }
            if (senha == null || senha.trim().isEmpty()) {
                throw new BusinessException("Senha não pode ser vazia");
            }

            boolean loginValido = usuarioDAO.validarLogin(nome, senha);

            if (loginValido) {
                logger.info("Login validado com sucesso para usuário: {}", nome);
            } else {
                logger.warn("Tentativa de login inválida para usuário: {}", nome);
            }

            return loginValido;

        } catch (DAOException e) {
            logger.error("Erro ao validar login", e);
            throw new BusinessException("Erro ao validar login: " + e.getMessage(), e);
        }
    }

    public Usuario buscarPorNome(String nome) throws BusinessException {
        logger.info("Buscando usuário por nome: {}", nome);
        try {
            if (nome == null || nome.trim().isEmpty()) {
                throw new BusinessException("Nome não pode ser vazio");
            }

            Optional<Usuario> usuario = usuarioDAO.buscarPorNome(nome);
            return usuario.orElse(null);

        } catch (DAOException e) {
            logger.error("Erro ao buscar usuário por nome", e);
            throw new BusinessException("Erro ao buscar usuário: " + e.getMessage(), e);
        }
    }

    public List<Usuario> listarTodos() throws BusinessException {
        logger.info("Listando todos os usuários");
        try {
            return usuarioDAO.listarTodos();
        } catch (DAOException e) {
            logger.error("Erro ao listar usuários", e);
            throw new BusinessException("Erro ao listar usuários: " + e.getMessage(), e);
        }
    }

    public void atualizarUsuario(Usuario usuario) throws BusinessException {
        logger.info("Atualizando usuário: {}", usuario.getNome());
        try {
            validarUsuario(usuario);

            if (usuario.getId() == null) {
                throw new BusinessException("ID do usuário é obrigatório para atualização");
            }

            usuarioDAO.atualizar(usuario);
            logger.info("Usuário atualizado com sucesso - ID: {}", usuario.getId());
        } catch (DAOException e) {
            logger.error("Erro ao atualizar usuário", e);
            throw new BusinessException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    public void excluirUsuario(Long id) throws BusinessException {
        logger.info("Excluindo usuário com ID: {}", id);
        try {
            if (id == null) {
                throw new BusinessException("ID do usuário é obrigatório");
            }

            usuarioDAO.excluir(id);
            logger.info("Usuário excluído com sucesso - ID: {}", id);
        } catch (DAOException e) {
            logger.error("Erro ao excluir usuário", e);
            throw new BusinessException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    private void validarUsuario(Usuario usuario) throws BusinessException {
        if (usuario == null) {
            throw new BusinessException("Usuário não pode ser nulo");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do usuário é obrigatório");
        }

        if (usuario.getNome().length() < 3) {
            throw new BusinessException("Nome do usuário deve ter pelo menos 3 caracteres");
        }

        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new BusinessException("Senha é obrigatória");
        }

        if (usuario.getSenha().length() < 4) {
            throw new BusinessException("Senha deve ter pelo menos 4 caracteres");
        }
    }
}
