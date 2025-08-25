package com.controlfinanceiro.view;

import com.controlfinanceiro.controller.RelatorioController;
import com.controlfinanceiro.controller.TransacaoController;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.enums.TipoTransacao;
import com.controlfinanceiro.exception.BusinessException;
import com.controlfinanceiro.util.FormatUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

public class GerarRelatorio extends JPanel {
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JComboBox<String> cmbTipoRelatorio;
    private JTable tabelaRelatorio;
    private DefaultTableModel modeloTabela;
    private JButton btnGerar;
    private JButton btnLimpar;
    private JButton btnExportar;
    private JLabel lblTotalReceitas;
    private JLabel lblTotalDespesas;
    private JLabel lblSaldoFinal;

    // Controllers
    private final TransacaoController transacaoController;

    public GerarRelatorio(RelatorioController relatorioController) {
        this.transacaoController = new TransacaoController();
        initComponents();
        setupLayout();
        setupEventos();
    }
    
    private void initComponents() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtDataInicio = new JTextField(15);
        txtDataFim = new JTextField(15);

        // Definir datas padrão (mês atual)
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        txtDataInicio.setText(inicioMes.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtDataFim.setText(hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        String[] tiposRelatorio = {"Todas as Transações", "Apenas Receitas", "Apenas Despesas", "Por Categoria"};
        cmbTipoRelatorio = new JComboBox<>(tiposRelatorio);
        
        btnGerar = new JButton("Gerar Relatório");
        btnLimpar = new JButton("Limpar");
        btnExportar = new JButton("Exportar");

        // Labels para totais
        lblTotalReceitas = new JLabel("Total Receitas: R$ 0,00");
        lblTotalDespesas = new JLabel("Total Despesas: R$ 0,00");
        lblSaldoFinal = new JLabel("Saldo: R$ 0,00");

        lblTotalReceitas.setForeground(new Color(46, 204, 113));
        lblTotalDespesas.setForeground(new Color(231, 76, 60));
        lblSaldoFinal.setFont(new Font("Arial", Font.BOLD, 14));

        // Configurar tabela
        String[] colunas = {"Data", "Descrição", "Categoria", "Tipo", "Valor"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaRelatorio = new JTable(modeloTabela);
        tabelaRelatorio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar cores das linhas baseadas no tipo
        tabelaRelatorio.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String tipo = (String) table.getValueAt(row, 3);
                    if ("RECEITA".equals(tipo)) {
                        c.setBackground(new Color(232, 245, 233));
                    } else if ("DESPESA".equals(tipo)) {
                        c.setBackground(new Color(255, 235, 238));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior com título
        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitulo = new JLabel("Gerar Relatórios Financeiros");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(155, 89, 182));
        painelTitulo.add(lblTitulo);

        // Painel de filtros
        JPanel painelFiltros = new JPanel(new GridBagLayout());
        painelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 1: Datas
        gbc.gridx = 0; gbc.gridy = 0;
        painelFiltros.add(new JLabel("Data Início:"), gbc);
        gbc.gridx = 1;
        painelFiltros.add(txtDataInicio, gbc);
        gbc.gridx = 2;
        painelFiltros.add(new JLabel("Data Fim:"), gbc);
        gbc.gridx = 3;
        painelFiltros.add(txtDataFim, gbc);

        // Linha 2: Tipo de relatório
        gbc.gridx = 0; gbc.gridy = 1;
        painelFiltros.add(new JLabel("Tipo de Relatório:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelFiltros.add(cmbTipoRelatorio, gbc);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        btnGerar.setBackground(new Color(155, 89, 182));
        btnGerar.setForeground(Color.WHITE);
        btnLimpar.setBackground(new Color(149, 165, 166));
        btnLimpar.setForeground(Color.WHITE);
        btnExportar.setBackground(new Color(52, 152, 219));
        btnExportar.setForeground(Color.WHITE);

        painelBotoes.add(btnGerar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnExportar);

        // Painel de totais
        JPanel painelTotais = new JPanel(new GridLayout(1, 3, 20, 5));
        painelTotais.setBorder(BorderFactory.createTitledBorder("Resumo"));
        painelTotais.add(lblTotalReceitas);
        painelTotais.add(lblTotalDespesas);
        painelTotais.add(lblSaldoFinal);

        // Painel superior completo
        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelFiltros, BorderLayout.CENTER);
        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);

        // Painel inferior com totais
        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(painelTotais, BorderLayout.NORTH);

        // Adicionar à tela
        add(painelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabelaRelatorio), BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }
    
    private void setupEventos() {
        btnGerar.addActionListener(e -> gerarRelatorio());
        btnLimpar.addActionListener(e -> limparRelatorio());
        btnExportar.addActionListener(e -> exportarRelatorio());
    }
    
    private void gerarRelatorio() {
        try {
            LocalDate dataInicio = LocalDate.parse(txtDataInicio.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalDate dataFim = LocalDate.parse(txtDataFim.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String tipoRelatorio = (String) cmbTipoRelatorio.getSelectedItem();

            if (dataInicio.isAfter(dataFim)) {
                JOptionPane.showMessageDialog(this, "Data de início deve ser anterior à data fim!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Transacao> transacoes = obterTransacoes(dataInicio, dataFim, tipoRelatorio);
            preencherTabela(transacoes);
            calcularTotais(transacoes);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida! Use o formato dd/MM/yyyy",
                "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Transacao> obterTransacoes(LocalDate dataInicio, LocalDate dataFim, String tipoRelatorio)
            throws BusinessException {
        List<Transacao> todasTransacoes = transacaoController.listarTransacoesPorPeriodo(dataInicio, dataFim);

        switch (tipoRelatorio) {
            case "Apenas Receitas":
                return todasTransacoes.stream()
                    .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                    .collect(Collectors.toList());
            case "Apenas Despesas":
                return todasTransacoes.stream()
                    .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                    .collect(Collectors.toList());
            case "Por Categoria":
                // Para relatório por categoria, retornar todas e agrupar na exibição
                return todasTransacoes;
            default: // "Todas as Transações"
                return todasTransacoes;
        }
    }
    
    private void preencherTabela(List<Transacao> transacoes) {
        modeloTabela.setRowCount(0);

        for (Transacao transacao : transacoes) {
            Object[] row = {
                transacao.getDataTransacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                transacao.getDescricao(),
                transacao.getCategoria() != null ? transacao.getCategoria().getNome() : "Sem categoria",
                transacao.getTipo().getDescricao(),
                FormatUtils.formatarValor(transacao.getValor())
            };
            modeloTabela.addRow(row);
        }

        if (transacoes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma transação encontrada para o período selecionado!",
                "Informação", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void calcularTotais(List<Transacao> transacoes) {
        BigDecimal totalReceitas = transacoes.stream()
            .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
            .map(Transacao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
            .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
            .map(Transacao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        lblTotalReceitas.setText("Total Receitas: " + FormatUtils.formatarValor(totalReceitas));
        lblTotalDespesas.setText("Total Despesas: " + FormatUtils.formatarValor(totalDespesas));
        lblSaldoFinal.setText("Saldo: " + FormatUtils.formatarValor(saldo));

        // Definir cor do saldo
        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            lblSaldoFinal.setForeground(new Color(46, 204, 113)); // Verde
        } else if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            lblSaldoFinal.setForeground(new Color(231, 76, 60)); // Vermelho
        } else {
            lblSaldoFinal.setForeground(Color.BLACK);
        }
    }
    
    private void limparRelatorio() {
        modeloTabela.setRowCount(0);

        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        txtDataInicio.setText(inicioMes.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtDataFim.setText(hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        cmbTipoRelatorio.setSelectedIndex(0);

        lblTotalReceitas.setText("Total Receitas: R$ 0,00");
        lblTotalDespesas.setText("Total Despesas: R$ 0,00");
        lblSaldoFinal.setText("Saldo: R$ 0,00");
        lblSaldoFinal.setForeground(Color.BLACK);
    }
    
    private void exportarRelatorio() {
        if (modeloTabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Não há dados para exportar! Gere um relatório primeiro.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório");
        fileChooser.setSelectedFile(new java.io.File("relatorio_financeiro.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                exportarParaCSV(fileToSave);
                JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarParaCSV(java.io.File arquivo) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(arquivo, StandardCharsets.UTF_8)) {
            // Cabeçalho
            writer.println("Data,Descrição,Categoria,Tipo,Valor");

            // Dados
            for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                StringBuilder linha = new StringBuilder();
                for (int j = 0; j < modeloTabela.getColumnCount(); j++) {
                    if (j > 0) linha.append(",");
                    String valor = modeloTabela.getValueAt(i, j).toString();
                    linha.append("\"").append(valor.replace("\"", "\"\"")).append("\"");
                }
                writer.println(linha.toString());
            }

            // Totais
            writer.println();
            writer.println("RESUMO");
            writer.println(lblTotalReceitas.getText());
            writer.println(lblTotalDespesas.getText());
            writer.println(lblSaldoFinal.getText());
        }
    }
}
