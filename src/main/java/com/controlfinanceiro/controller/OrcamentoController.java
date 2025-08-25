package com.controlfinanceiro.controller;

import com.controlfinanceiro.dao.OrcamentoDAO;
import com.controlfinanceiro.dao.impl.OrcamentoDAOImpl;
import com.controlfinanceiro.exception.BusinessException;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.model.Orcamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrcamentoController {

    private static final Logger logger = LoggerFactory.getLogger(OrcamentoController.class);

    private final OrcamentoDAO orcamentoDAO;

    public OrcamentoController() {
        this.orcamentoDAO = new OrcamentoDAOImpl();
    }
    //Slavar um novo orçamento

    public void salvarOrcamento(Orcamento orcamento) throws BusinessException {
        logger.info("Salvando orçamento para categoria {} - periodo {}/{}",
                orcamento.getCategoriaId(), orcamento.getMes(), orcamento.getAno());

        try {
            validarOrcamento(orcamento);
            orcamentoDAO.inserir(orcamento);
            logger.info("Orçamento salvo com sucesso - ID: {}", orcamento.getId());
        } catch (DAOException e) {
            logger.error("Erro ao salvar orçamento", e);
            throw new BusinessException("Erro ao salvar orçamento: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza um orçamento existente
     */
    public void atualizarOrcamento(Orcamento orcamento) throws BusinessException {
        logger.info("Atualizando orçamento ID: {}", orcamento.getId());

        try {
            validarOrcamento(orcamento);
            validarOrcamentoExiste(orcamento.getId());
            orcamentoDAO.atualizar(orcamento);
            logger.info("Orçamento atualizado com sucesso");
        } catch (DAOException e) {
            logger.error("Erro ao atualizar orçamento", e);
            throw new BusinessException("Erro ao atualizar orçamento: " + e.getMessage(), e);
        }
    }

    /**
     * Exclui um orçamento
     */
    public void excluirOrcamento(Long id) throws BusinessException {
        logger.info("Excluindo orçamento ID: {}", id);

        try {
            validarOrcamentoExiste(id);
            orcamentoDAO.excluir(id);
            logger.info("Orçamento excluído com sucesso");
        } catch (DAOException e) {
            logger.error("Erro ao excluir orçamento", e);
            throw new BusinessException("Erro ao excluir orçamento: " + e.getMessage(), e);
        }
    }

    /**
     * Busca orçamento por ID
     */
    public Orcamento buscarPorId(Long id) throws BusinessException {
        logger.debug("Buscando orçamento por ID: {}", id);

        try {
            Optional<Orcamento> orcamentoOpt = orcamentoDAO.buscarPorId(id);
            if (orcamentoOpt.isEmpty()) {
                throw new BusinessException("Orçamento não encontrado");
            }
            return orcamentoOpt.get();
        } catch (DAOException e) {
            logger.error("Erro ao buscar orçamento", e);
            throw new BusinessException("Erro ao buscar orçamento: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todos os orçamentos
     */
    public List<Orcamento> listarTodos() throws BusinessException {
        logger.debug("Listando todos os orçamentos");

        try {
            return orcamentoDAO.listarTodos();
        } catch (DAOException e) {
            logger.error("Erro ao listar orçamentos", e);
            throw new BusinessException("Erro ao listar orçamentos: " + e.getMessage(), e);
        }
    }

    /**
     * Lista orçamentos por período
     */
    public List<Orcamento> listarPorPeriodo(int mes, int ano) throws BusinessException {
        logger.debug("Listando orçamentos para {}/{}", mes, ano);

        try {
            validarPeriodo(mes, ano);
            return orcamentoDAO.buscarPorPeriodo(mes, ano);
        } catch (DAOException e) {
            logger.error("Erro ao listar orçamentos por período", e);
            throw new BusinessException("Erro ao listar orçamentos por período: " + e.getMessage(), e);
        }
    }

    /**
     * Lista orçamentos por categoria
     */
    public List<Orcamento> listarPorCategoria(Long categoriaId) throws BusinessException {
        logger.debug("Listando orçamentos por categoria: {}", categoriaId);

        try {
            return orcamentoDAO.buscarPorCategoria(categoriaId);
        } catch (DAOException e) {
            logger.error("Erro ao listar orçamentos por categoria", e);
            throw new BusinessException("Erro ao listar orçamentos por categoria: " + e.getMessage(), e);
        }
    }

    // Métodos de validação
    private void validarOrcamento(Orcamento orcamento) throws BusinessException {
        if (orcamento == null) {
            throw new BusinessException("Orçamento não pode ser nulo");
        }
        if (orcamento.getCategoriaId() == null) {
            throw new BusinessException("Categoria é obrigatória");
        }
        if (orcamento.getValorLimite() == null || orcamento.getValorLimite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor limite deve ser maior que zero");
        }
        validarPeriodo(orcamento.getMes(), orcamento.getAno());
    }

    private void validarPeriodo(int mes, int ano) throws BusinessException {
        if (mes < 1 || mes > 12) {
            throw new BusinessException("Mês deve estar entre 1 e 12");
        }
        if (ano < 2020) {
            throw new BusinessException("Ano deve ser maior que 2020");
        }
    }

    private void validarOrcamentoExiste(Long id) throws BusinessException {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido");
        }
        try {
            Optional<Orcamento> orcamentoOpt = orcamentoDAO.buscarPorId(id);
            if (orcamentoOpt.isEmpty()) {
                throw new BusinessException("Orçamento não encontrado");
            }
        } catch (DAOException e) {
            logger.error("Erro ao validar existência do orçamento", e);
            throw new BusinessException("Erro ao validar orçamento: " + e.getMessage(), e);
        }
    }
}
