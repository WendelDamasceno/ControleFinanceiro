package com.controlfinanceiro.controller;

import com.controlfinanceiro.dao.TransacaoDAO;
import com.controlfinanceiro.dao.CategoriaDAO;
import com.controlfinanceiro.dao.impl.TransacaoDAOImpl;
import com.controlfinanceiro.dao.impl.CategoriaDAOImpl;
import com.controlfinanceiro.exception.DAOException;
import com.controlfinanceiro.exception.BusinessException;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.enums.TipoTransacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransacaoController {
    private static final Logger logger = LoggerFactory.getLogger(TransacaoController.class);
    private final TransacaoDAO transacaoDAO;
    private final CategoriaDAO categoriaDAO;

    public TransacaoController() {
        this.transacaoDAO = new TransacaoDAOImpl();
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    public void salvarTransacao(Transacao transacao) throws BusinessException{
        logger.info("Salvando Transacao: {} - R$ {}", transacao.getDescricao(), transacao.getValor());
        try{
            validarTransacao(transacao);
            transacaoDAO.inserir(transacao);
            logger.info("Transacao salva com sucesso - ID: {}", transacao.getId());
        } catch (DAOException e) {
            logger.error("Erro ao salvar Transacao",e);
            throw new BusinessException("Erro ao salvar transação: " + e.getMessage(),e);
        }
    }

    public void atualizarTransacao(Transacao transacao) throws BusinessException {
        logger.info("Atualizando Transacao ID: {}", transacao.getId());
        try {
            validarTransacao(transacao);
            validarTransacaoExiste(transacao.getId());
            transacaoDAO.atualizar(transacao);
            logger.info("Transação atualizada com sucesso");
        }catch (DAOException e){
            logger.error("Erro ao atualizar Transacao", e);
            throw new BusinessException("Erro ao atualizar transação: " + e.getMessage(), e);
        }
    }

    public void excluirTransacao(Long id) throws BusinessException {
        logger.info("Excluindo Transacao ID: {}", id);
        try {
            validarTransacaoExiste(id);
            transacaoDAO.excluir(id);
            logger.info("Transação excluída com sucesso");
        }catch (DAOException e){
            logger.error("Erro ao excluir Transacao", e);
            throw new BusinessException("Erro ao excluir transação: " + e.getMessage(), e);
        }
    }

    public Transacao buscarPorId(Long id) throws BusinessException {
        logger.info("Buscando Transacao ID: {}", id);
        try {
            Optional<Transacao> transacaoOpt = transacaoDAO.buscarPorId(id);
            if (transacaoOpt.isEmpty()) {
                throw new BusinessException("Transação não encontrada com ID: " + id);
            }
            return transacaoOpt.get();
        } catch (DAOException e) {
            logger.error("Erro ao buscar Transacao por ID", e);
            throw new BusinessException("Erro ao buscar transação: " + e.getMessage(), e);
        }
    }

    public List<Transacao> listarPorPeriodo(LocalDate inicio, LocalDate fim) throws BusinessException {
        logger.debug("Listando Transações por período: {} a {}", inicio, fim);
        try {
            return transacaoDAO.listarPorPeriodo(inicio, fim);
        } catch (DAOException e) {
            logger.error("Erro ao listar Transacoes por período", e);
            throw new BusinessException("Erro ao listar transações: " + e.getMessage(), e);
        }
    }

    public List<Transacao> listarPorCategoria(Long categoriaId) throws BusinessException{
        logger.debug("Listando Transações por Categoria ID: {}", categoriaId);
        try {
            return transacaoDAO.buscarPorCategoria(categoriaId);
        } catch (DAOException e) {
            logger.error("Erro ao listar Transacoes por categoria", e);
            throw new BusinessException("Erro ao listar transações por categoria: " + e.getMessage(), e);
        }
    }

    public BigDecimal calcularTotalPorTipo(TipoTransacao tipo) throws BusinessException{
        logger.debug("Calculando total por tipo: {}", tipo);
        try {
            return transacaoDAO.calcularTotalPorTipo(tipo);
        } catch (DAOException e) {
            logger.error("Erro ao calcular total por tipo", e);
            throw new BusinessException("Erro ao calcular total por tipo: " + e.getMessage(), e);
        }
    }

    private void validarTransacao(Transacao transacao) throws BusinessException{
        if (transacao == null) {
            throw new BusinessException("Transação não pode ser nula");
        }
        if (transacao.getDescricao() == null || transacao.getDescricao().trim().isEmpty()) {
            throw new BusinessException("Descrição é obrigatória");
        }
        if( transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Valor deve ser maior que zero");
        }
        if (transacao.getTipo() == null){
            throw new BusinessException("Tipo de transação é obrigatório");
        }
        if(transacao.getDataTransacao() == null){
            throw new BusinessException("Data de transação é obrigatória");
        }
    }

    public List<Transacao> listarTransacoes() throws BusinessException {
        logger.debug("Listando todas as transações");
        try {
            return transacaoDAO.listarTodas();
        } catch (DAOException e) {
            logger.error("Erro ao listar todas as transações", e);
            throw new BusinessException("Erro ao listar transações: " + e.getMessage(), e);
        }
    }

    public List<Transacao> listarAtivas() throws BusinessException {
        logger.debug("Listando todas as transações ativas");
        try {
            return transacaoDAO.listarTodas();
        } catch (DAOException e) {
            logger.error("Erro ao listar transações ativas", e);
            throw new BusinessException("Erro ao listar transações: " + e.getMessage(), e);
        }
    }

    public List<Transacao> listarTransacoesPorTipo(TipoTransacao tipo) throws BusinessException {
        logger.debug("Listando transações por tipo: {}", tipo);
        try {
            return transacaoDAO.listarPorTipo(tipo);
        } catch (DAOException e) {
            logger.error("Erro ao listar transações por tipo", e);
            throw new BusinessException("Erro ao listar transações por tipo: " + e.getMessage(), e);
        }
    }

    public List<Transacao> listarTransacoesPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws BusinessException {
        logger.debug("Listando transações por período: {} a {}", dataInicio, dataFim);
        try {
            return transacaoDAO.listarPorPeriodo(dataInicio, dataFim);
        } catch (DAOException e) {
            logger.error("Erro ao listar transações por período", e);
            throw new BusinessException("Erro ao listar transações por período: " + e.getMessage(), e);
        }
    }

    /**
     * Lista transações por período filtradas pelo usuário logado
     */
    public List<Transacao> listarTransacoesPorPeriodoDoUsuario(LocalDate dataInicio, LocalDate dataFim) throws BusinessException {
        Long idUsuario = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();

        if (idUsuario == null) {
            throw new BusinessException("Nenhum usuário logado encontrado.");
        }

        logger.info("Listando transações do usuário {} no período: {} a {}", idUsuario, dataInicio, dataFim);

        try {
            if (dataInicio == null || dataFim == null) {
                throw new BusinessException("Período deve ser informado");
            }

            if (dataInicio.isAfter(dataFim)) {
                throw new BusinessException("Data inicial não pode ser posterior à data final");
            }

            // Agora usando o método que filtra por usuário
            return transacaoDAO.buscarPorUsuarioEPeriodo(idUsuario, dataInicio, dataFim);

        } catch (DAOException e) {
            logger.error("Erro ao listar transações por período", e);
            throw new BusinessException("Erro ao listar transações: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todas as transações do usuário logado
     */
    public List<Transacao> listarTransacoesDoUsuario() throws BusinessException {
        Long idUsuario = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();

        if (idUsuario == null) {
            throw new BusinessException("Nenhum usuário logado encontrado.");
        }

        logger.info("Listando todas as transações do usuário {}", idUsuario);

        try {
            // Agora usando o método que filtra por usuário
            return transacaoDAO.buscarPorUsuario(idUsuario);

        } catch (DAOException e) {
            logger.error("Erro ao listar transações", e);
            throw new BusinessException("Erro ao listar transações: " + e.getMessage(), e);
        }
    }

    private void validarTransacaoExiste(Long id) throws BusinessException {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido");
        }
        try {
            Optional<Transacao> transacaoOpt = transacaoDAO.buscarPorId(id);
            if (transacaoOpt.isEmpty()) {
                throw new BusinessException("Transação não encontrada com ID: " + id);
            }
        } catch (DAOException e) {
            logger.error("Erro ao validar existência da transação", e);
            throw new BusinessException("Erro ao validar transação: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula o saldo atual do usuário logado
     */
    public BigDecimal calcularSaldoDoUsuario() throws BusinessException {
        Long idUsuario = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();

        if (idUsuario == null) {
            throw new BusinessException("Nenhum usuário logado encontrado.");
        }

        logger.info("Calculando saldo do usuário {}", idUsuario);

        try {
            List<Transacao> todasTransacoes = listarTransacoesDoUsuario();

            BigDecimal totalReceitas = todasTransacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDespesas = todasTransacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            return totalReceitas.subtract(totalDespesas);

        } catch (Exception e) {
            logger.error("Erro ao calcular saldo do usuário", e);
            throw new BusinessException("Erro ao calcular saldo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém estatísticas do mês atual para o usuário logado
     */
    public EstatisticasMensais obterEstatisticasMensaisDoUsuario() throws BusinessException {
        Long idUsuario = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();

        if (idUsuario == null) {
            throw new BusinessException("Nenhum usuário logado encontrado.");
        }

        logger.info("Obtendo estatísticas mensais do usuário {}", idUsuario);

        try {
            LocalDate hoje = LocalDate.now();
            LocalDate inicioMes = hoje.withDayOfMonth(1);
            LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

            List<Transacao> transacoesMes = listarTransacoesPorPeriodoDoUsuario(inicioMes, fimMes);

            BigDecimal receitasMes = transacoesMes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal despesasMes = transacoesMes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoMes = receitasMes.subtract(despesasMes);

            return new EstatisticasMensais(receitasMes, despesasMes, saldoMes, transacoesMes.size());

        } catch (Exception e) {
            logger.error("Erro ao obter estatísticas mensais", e);
            throw new BusinessException("Erro ao obter estatísticas: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém as últimas transações do usuário logado
     */
    public List<Transacao> obterUltimasTransacoesDoUsuario(int limite) throws BusinessException {
        Long idUsuario = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();

        if (idUsuario == null) {
            throw new BusinessException("Nenhum usuário logado encontrado.");
        }

        logger.info("Obtendo últimas {} transações do usuário {}", limite, idUsuario);

        try {
            List<Transacao> todasTransacoes = listarTransacoesDoUsuario();

            return todasTransacoes.stream()
                .sorted((t1, t2) -> t2.getDataTransacao().compareTo(t1.getDataTransacao()))
                .limit(limite)
                .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            logger.error("Erro ao obter últimas transações", e);
            throw new BusinessException("Erro ao obter transações: " + e.getMessage(), e);
        }
    }

    /**
     * Classe auxiliar para estatísticas mensais
     */
    public static class EstatisticasMensais {
        private final BigDecimal receitas;
        private final BigDecimal despesas;
        private final BigDecimal saldo;
        private final int totalTransacoes;

        public EstatisticasMensais(BigDecimal receitas, BigDecimal despesas, BigDecimal saldo, int totalTransacoes) {
            this.receitas = receitas;
            this.despesas = despesas;
            this.saldo = saldo;
            this.totalTransacoes = totalTransacoes;
        }

        public BigDecimal getReceitas() { return receitas; }
        public BigDecimal getDespesas() { return despesas; }
        public BigDecimal getSaldo() { return saldo; }
        public int getTotalTransacoes() { return totalTransacoes; }
    }
}
