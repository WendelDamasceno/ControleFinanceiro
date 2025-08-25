package com.controlfinanceiro.controller;

import com.controlfinanceiro.dao.CategoriaDAO;
import com.controlfinanceiro.dao.impl.CategoriaDAOImpl;
import com.controlfinanceiro.exception.BusinessException;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.model.Categoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CategoriaController {
    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);
    private final CategoriaDAO categoriaDAO;
    public CategoriaController(){
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    //Salva uma nova categoria

    public void salvarCategoria(Categoria categoria) throws BusinessException {
        logger.info("Salvando categoria: {}", categoria.getNome());
        try{
            validarCategoria(categoria);
            categoriaDAO.inserir(categoria);
            logger.info("Categoria salva com sucesso - ID: {}", categoria.getId());
        }catch (DAOException e){
            logger.error("Erro ao salvar categoria", e);
            throw new BusinessException("Erro ao salvar a categoria: " + e.getMessage(), e);
        }
    }

    //atualizar uma categoria existente

    public void atualizarCategoria(Categoria categoria) throws BusinessException {
        logger.info("Atualizando categoria: {}", categoria.getId());
        try{
            validarCategoria(categoria);
            validarCategoriaExiste(categoria.getId());
            categoriaDAO.atualizar(categoria);
            logger.info("Categoria atualizada com sucesso");
        } catch (DAOException e) {
            logger.error("Erro ao atualizar categoria", e);
            throw new BusinessException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    //Excluir uma categoria

    public void excluirCategoria(Long id) throws BusinessException {
        logger.info("Excluindo categoria: {}", id);
        try{
            validarCategoriaExiste(id);
            categoriaDAO.excluir(id);
            logger.info("Categoria excluida com sucesso");
        } catch (DAOException e) {
            logger.error("Erro ao excluir categoria", e);
            throw new BusinessException("Erro ao excluir categoria: " + e.getMessage(), e);
        }
    }

     //Busca categoria por ID

    public Categoria buscarPorId(Long id) throws BusinessException {
        logger.debug("Buscando categoria por ID: {}", id);

        try {
            Optional<Categoria> categoriaOpt = categoriaDAO.buscarPorId(id);
            if (categoriaOpt.isEmpty()) {
                throw new BusinessException("Categoria não encontrada");
            }
            return categoriaOpt.get();
        } catch (DAOException e) {
            logger.error("Erro ao buscar categoria", e);
            throw new BusinessException("Erro ao buscar categoria: " + e.getMessage(), e);
        }
    }

     //Lista todas as categorias

    public List<Categoria> listarTodas() throws BusinessException {
        logger.debug("Listando todas as categorias");

        try {
            return categoriaDAO.listarTodas();
        } catch (DAOException e) {
            logger.error("Erro ao listar categorias", e);
            throw new BusinessException("Erro ao listar categorias: " + e.getMessage(), e);
        }
    }

     // Lista categorias ativas

    public List<Categoria> listarAtivas() throws BusinessException {
        logger.debug("Listando categorias ativas");

        try {
            return categoriaDAO.listarAtivas();
        } catch (DAOException e) {
            logger.error("Erro ao listar categorias ativas", e);
            throw new BusinessException("Erro ao listar categorias ativas: " + e.getMessage(), e);
        }
    }

    // Lista todas as categorias (usado em comboboxes)
    public List<Categoria> listarCategorias() throws BusinessException {
        logger.debug("Listando todas as categorias");
        try {
            return categoriaDAO.listarTodas();
        } catch (DAOException e) {
            logger.error("Erro ao listar categorias", e);
            throw new BusinessException("Erro ao listar categorias: " + e.getMessage(), e);
        }
    }

    // Métodos de validação
    private void validarCategoria(Categoria categoria) throws BusinessException {
        if (categoria == null) {
            throw new BusinessException("Categoria não pode ser nula");
        }
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome da categoria é obrigatório");
        }
        if (categoria.getNome().length() > 100) {
            throw new BusinessException("Nome da categoria não pode ter mais de 100 caracteres");
        }
    }

    private void validarCategoriaExiste(Long id) throws BusinessException {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido");
        }
        try {
            Optional<Categoria> categoriaOpt = categoriaDAO.buscarPorId(id);
            if (categoriaOpt.isEmpty()) {
                throw new BusinessException("Categoria não encontrada");
            }
        } catch (DAOException e) {
            logger.error("Erro ao validar existência da categoria", e);
            throw new BusinessException("Erro ao validar categoria: " + e.getMessage(), e);
        }
    }
}
