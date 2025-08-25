package com.controlfinanceiro.controller;

import com.controlfinanceiro.dao.TransacaoDAO;
import com.controlfinanceiro.dao.impl.TransacaoDAOImpl;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.enums.TipoTransacao;
import com.controlfinanceiro.util.SessaoUsuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsável por fornecer dados específicos do usuário logado para o dashboard
 */
public class DashboardController {

    private final TransacaoDAO transacaoDAO;

    public DashboardController() {
        this.transacaoDAO = new TransacaoDAOImpl();
    }

    /**
     * Retorna estatísticas do mês atual para o usuário logado
     */
    public EstatisticasDashboard getEstatisticasMesAtual() {
        Long usuarioId = SessaoUsuario.getInstance().getIdUsuarioLogado();
        if (usuarioId == null) {
            return new EstatisticasDashboard();
        }

        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate fimMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        try {
            BigDecimal receitas = transacaoDAO.calcularTotalPorUsuarioETipo(usuarioId, TipoTransacao.RECEITA);
            BigDecimal despesas = transacaoDAO.calcularTotalPorUsuarioETipo(usuarioId, TipoTransacao.DESPESA);

            List<Transacao> transacoesMes = transacaoDAO.buscarPorUsuarioEPeriodo(usuarioId, inicioMes, fimMes);

            return new EstatisticasDashboard(receitas, despesas, transacoesMes.size(), calcularSaldo(receitas, despesas));

        } catch (DAOException e) {
            e.printStackTrace();
            return new EstatisticasDashboard();
        }
    }

    /**
     * Retorna estatísticas do ano atual para o usuário logado
     */
    public EstatisticasDashboard getEstatisticasAnoAtual() {
        Long usuarioId = SessaoUsuario.getInstance().getIdUsuarioLogado();
        if (usuarioId == null) {
            return new EstatisticasDashboard();
        }

        LocalDate inicioAno = LocalDate.now().withDayOfYear(1);
        LocalDate fimAno = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());

        try {
            List<Transacao> transacoesAno = transacaoDAO.buscarPorUsuarioEPeriodo(usuarioId, inicioAno, fimAno);

            BigDecimal receitas = transacoesAno.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal despesas = transacoesAno.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new EstatisticasDashboard(receitas, despesas, transacoesAno.size(), calcularSaldo(receitas, despesas));

        } catch (DAOException e) {
            e.printStackTrace();
            return new EstatisticasDashboard();
        }
    }

    /**
     * Retorna as últimas transações do usuário logado
     */
    public List<Transacao> getUltimasTransacoes(int limite) {
        Long usuarioId = SessaoUsuario.getInstance().getIdUsuarioLogado();
        if (usuarioId == null) {
            return List.of();
        }

        try {
            return transacaoDAO.buscarUltimasTransacoesPorUsuario(usuarioId, limite);
        } catch (DAOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Verifica se o usuário logado é um usuário novo (sem transações)
     */
    public boolean isUsuarioNovo() {
        Long usuarioId = SessaoUsuario.getInstance().getIdUsuarioLogado();
        if (usuarioId == null) {
            return true;
        }

        try {
            List<Transacao> transacoes = transacaoDAO.buscarPorUsuario(usuarioId);
            return transacoes.isEmpty();
        } catch (DAOException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Calcula o saldo (receitas - despesas)
     */
    private BigDecimal calcularSaldo(BigDecimal receitas, BigDecimal despesas) {
        if (receitas == null) receitas = BigDecimal.ZERO;
        if (despesas == null) despesas = BigDecimal.ZERO;
        return receitas.subtract(despesas);
    }

    /**
     * Classe interna para representar estatísticas do dashboard
     */
    public static class EstatisticasDashboard {
        private BigDecimal receitas;
        private BigDecimal despesas;
        private int totalTransacoes;
        private BigDecimal saldo;

        public EstatisticasDashboard() {
            this.receitas = BigDecimal.ZERO;
            this.despesas = BigDecimal.ZERO;
            this.totalTransacoes = 0;
            this.saldo = BigDecimal.ZERO;
        }

        public EstatisticasDashboard(BigDecimal receitas, BigDecimal despesas, int totalTransacoes, BigDecimal saldo) {
            this.receitas = receitas != null ? receitas : BigDecimal.ZERO;
            this.despesas = despesas != null ? despesas : BigDecimal.ZERO;
            this.totalTransacoes = totalTransacoes;
            this.saldo = saldo != null ? saldo : BigDecimal.ZERO;
        }

        // Getters
        public BigDecimal getReceitas() { return receitas; }
        public BigDecimal getDespesas() { return despesas; }
        public int getTotalTransacoes() { return totalTransacoes; }
        public BigDecimal getSaldo() { return saldo; }
    }
}
