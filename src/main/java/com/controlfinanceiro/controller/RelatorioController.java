package com.controlfinanceiro.controller;

import com.controlfinanceiro.dao.TransacaoDAO;
import com.controlfinanceiro.dao.OrcamentoDAO;
import com.controlfinanceiro.dao.CategoriaDAO;
import com.controlfinanceiro.dao.impl.TransacaoDAOImpl;
import com.controlfinanceiro.dao.impl.OrcamentoDAOImpl;
import com.controlfinanceiro.dao.impl.CategoriaDAOImpl;
import com.controlfinanceiro.exception.BusinessException;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.Orcamento;
import com.controlfinanceiro.model.Categoria;
import com.controlfinanceiro.model.enums.TipoTransacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RelatorioController {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioController.class);

    private final TransacaoDAO transacaoDAO;
    private final OrcamentoDAO orcamentoDAO;
    private final CategoriaDAO categoriaDAO;

    public RelatorioController() {
        this.transacaoDAO = new TransacaoDAOImpl();
        this.orcamentoDAO = new OrcamentoDAOImpl();
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    /**
     * Gera resumo financeiro geral
     */
    public Map<String, Object> gerarResumoFinanceiro() throws BusinessException {
        logger.info("Gerando resumo financeiro geral");

        try {
            // Obter usuário logado
            Long usuarioId = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();
            if (usuarioId == null) {
                throw new BusinessException("Nenhum usuário logado");
            }

            Map<String, Object> resumo = new HashMap<>();

            BigDecimal totalReceitas = transacaoDAO.calcularTotalPorUsuarioETipo(usuarioId, TipoTransacao.RECEITA);
            BigDecimal totalDespesas = transacaoDAO.calcularTotalPorUsuarioETipo(usuarioId, TipoTransacao.DESPESA);
            BigDecimal saldoAtual = totalReceitas.subtract(totalDespesas);

            resumo.put("totalReceitas", totalReceitas);
            resumo.put("totalDespesas", totalDespesas);
            resumo.put("saldoAtual", saldoAtual);

            logger.info("Resumo financeiro gerado com sucesso");
            return resumo;

        } catch (DAOException e) {
            logger.error("Erro ao gerar resumo financeiro", e);
            throw new BusinessException("Erro ao gerar resumo financeiro: " + e.getMessage(), e);
        }
    }

    /**
     * Gera resumo financeiro por período
     */
    public Map<String, Object> gerarResumoPorPeriodo(LocalDate inicio, LocalDate fim) throws BusinessException {
        logger.info("Gerando resumo financeiro para período: {} a {}", inicio, fim);

        validarPeriodo(inicio, fim);

        try {
            // Obter usuário logado
            Long usuarioId = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();
            if (usuarioId == null) {
                throw new BusinessException("Nenhum usuário logado");
            }

            Map<String, Object> resumo = new HashMap<>();

            List<Transacao> transacoes = transacaoDAO.buscarPorUsuarioEPeriodo(usuarioId, inicio, fim);
            BigDecimal totalReceitas = BigDecimal.ZERO;
            BigDecimal totalDespesas = BigDecimal.ZERO;

            for (Transacao transacao : transacoes) {
                if (transacao.getTipo() == TipoTransacao.RECEITA) {
                    totalReceitas = totalReceitas.add(transacao.getValor());
                } else {
                    totalDespesas = totalDespesas.add(transacao.getValor());
                }
            }

            BigDecimal saldo = totalReceitas.subtract(totalDespesas);

            resumo.put("periodo", Map.of("inicio", inicio, "fim", fim));
            resumo.put("totalReceitas", totalReceitas);
            resumo.put("totalDespesas", totalDespesas);
            resumo.put("saldo", saldo);
            resumo.put("quantidadeTransacoes", transacoes.size());
            resumo.put("transacoes", transacoes);

            return resumo;

        } catch (DAOException e) {
            logger.error("Erro ao gerar resumo por período", e);
            throw new BusinessException("Erro ao gerar resumo por período: " + e.getMessage(), e);
        }
    }

    /**
     * Gera relatório de orçamento vs gastos reais
     */
    public Map<String, Object> gerarRelatorioOrcamento(int mes, int ano) throws BusinessException {
        logger.info("Gerando relatório de orçamento para {}/{}", mes, ano);

        validarPeriodo(mes, ano);

        try {
            // Obter usuário logado
            Long usuarioId = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();
            if (usuarioId == null) {
                throw new BusinessException("Nenhum usuário logado");
            }

            Map<String, Object> relatorio = new HashMap<>();
            List<Orcamento> orcamentos = orcamentoDAO.buscarPorUsuarioEPeriodo(usuarioId, mes, ano);

            BigDecimal totalOrcado = BigDecimal.ZERO;
            BigDecimal totalGasto = BigDecimal.ZERO;

            LocalDate inicioMes = LocalDate.of(ano, mes, 1);
            LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

            for (Orcamento orcamento : orcamentos) {
                totalOrcado = totalOrcado.add(orcamento.getValorLimite());

                List<Transacao> transacoesCategoria = transacaoDAO.buscarPorUsuarioECategoria(usuarioId, orcamento.getCategoriaId());

                for (Transacao transacao : transacoesCategoria) {
                    if (!transacao.getDataTransacao().isBefore(inicioMes) &&
                        !transacao.getDataTransacao().isAfter(fimMes) &&
                        transacao.getTipo() == TipoTransacao.DESPESA) {
                        totalGasto = totalGasto.add(transacao.getValor());
                    }
                }
            }

            BigDecimal saldoOrcamento = totalOrcado.subtract(totalGasto);

            relatorio.put("mes", mes);
            relatorio.put("ano", ano);
            relatorio.put("totalOrcado", totalOrcado);
            relatorio.put("totalGasto", totalGasto);
            relatorio.put("saldoOrcamento", saldoOrcamento);
            relatorio.put("orcamentos", orcamentos);

            return relatorio;

        } catch (DAOException e) {
            logger.error("Erro ao gerar relatório de orçamento", e);
            throw new BusinessException("Erro ao gerar relatório de orçamento: " + e.getMessage(), e);
        }
    }

    /**
     * Gera relatório por categoria
     */
    public Map<String, Object> gerarRelatorioPorCategoria(Long categoriaId) throws BusinessException {
        logger.info("Gerando relatório por categoria: {}", categoriaId);

        try {
            // Obter usuário logado
            Long usuarioId = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();
            if (usuarioId == null) {
                throw new BusinessException("Nenhum usuário logado");
            }

            Map<String, Object> relatorio = new HashMap<>();

            List<Transacao> transacoes = transacaoDAO.buscarPorUsuarioECategoria(usuarioId, categoriaId);

            BigDecimal totalReceitas = transacoes.stream()
                    .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                    .map(Transacao::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDespesas = transacoes.stream()
                    .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                    .map(Transacao::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            relatorio.put("categoriaId", categoriaId);
            relatorio.put("totalReceitas", totalReceitas);
            relatorio.put("totalDespesas", totalDespesas);
            relatorio.put("saldo", totalReceitas.subtract(totalDespesas));
            relatorio.put("transacoes", transacoes);
            relatorio.put("quantidadeTransacoes", transacoes.size());

            logger.info("Relatório por categoria gerado com sucesso");
            return relatorio;

        } catch (DAOException e) {
            logger.error("Erro ao gerar relatório por categoria", e);
            throw new BusinessException("Erro ao gerar relatório por categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Gera relatório de totais por categoria
     */
    public Map<String, BigDecimal> gerarTotaisPorCategoria() throws BusinessException {
        logger.info("Gerando totais por categoria");

        try {
            // Obter usuário logado
            Long usuarioId = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();
            if (usuarioId == null) {
                throw new BusinessException("Nenhum usuário logado");
            }

            Map<String, BigDecimal> totais = new HashMap<>();
            List<Categoria> categorias = categoriaDAO.listarAtivas();

            for (Categoria categoria : categorias) {
                List<Transacao> transacoes = transacaoDAO.buscarPorUsuarioECategoria(usuarioId, categoria.getId());

                BigDecimal totalCategoria = BigDecimal.ZERO;
                for (Transacao transacao : transacoes) {
                    if (transacao.getTipo() == TipoTransacao.DESPESA) {
                        totalCategoria = totalCategoria.add(transacao.getValor());
                    } else {
                        totalCategoria = totalCategoria.subtract(transacao.getValor());
                    }
                }

                totais.put(categoria.getNome(), totalCategoria);
            }

            return totais;

        } catch (DAOException e) {
            logger.error("Erro ao gerar totais por categoria", e);
            throw new BusinessException("Erro ao gerar totais por categoria: " + e.getMessage(), e);
        }
    }

    // Métodos de validação
    private void validarPeriodo(LocalDate inicio, LocalDate fim) throws BusinessException {
        if (inicio == null || fim == null) {
            throw new BusinessException("Datas de início e fim são obrigatórias");
        }
        if (inicio.isAfter(fim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }
    }

    private void validarPeriodo(int mes, int ano) throws BusinessException {
        if (mes < 1 || mes > 12) {
            throw new BusinessException("Mês deve estar entre 1 e 12");
        }
        if (ano < 2020) {
            throw new BusinessException("Ano deve ser maior que 2020");
        }
    }
}
