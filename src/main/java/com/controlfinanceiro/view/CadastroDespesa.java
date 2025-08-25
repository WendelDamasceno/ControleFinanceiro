package com.controlfinanceiro.view;

import com.controlfinanceiro.controller.TransacaoController;
import com.controlfinanceiro.controller.CategoriaController;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.Categoria;
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

public class CadastroDespesa extends JPanel {
    private JTextField txtDescricao;
    private JTextField txtValor;
    private JTextField txtData;
    private JComboBox<Categoria> cmbCategoria;
    private JTextArea txtObservacoes;
    private JTable tabelaDespesas;
    private DefaultTableModel modeloTabela;
    private JButton btnSalvar;
    private JButton btnLimpar;
    private JButton btnExcluir;

    // Controllers
    private final TransacaoController transacaoController;
    private final CategoriaController categoriaController;

    // Referência à tela principal para atualizar dashboard
    private TelaPrincipal telaPrincipal;

    public CadastroDespesa(TransacaoController transacaoController, CategoriaController categoriaController) {
        this.transacaoController = transacaoController;
        this.categoriaController = categoriaController;
        initComponents();
        setupLayout();
        setupEventos();
        carregarCategorias();
        carregarDespesas();
    }
    
    // Método para definir referência à tela principal
    public void setTelaPrincipal(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
    }

    private void initComponents() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        txtDescricao = new JTextField(20);
        txtValor = new JTextField(15);
        txtData = new JTextField(15);
        txtData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtObservacoes = new JTextArea(3, 20);
        
        cmbCategoria = new JComboBox<>();

        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        txtObservacoes.setBorder(BorderFactory.createLoweredBevelBorder());
        
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar");
        btnExcluir = new JButton("Excluir");

        // Configurar tabela
        String[] colunas = {"ID", "Descrição", "Categoria", "Valor", "Data", "Observações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaDespesas = new JTable(modeloTabela);
        tabelaDespesas.getColumnModel().getColumn(0).setMaxWidth(50);
        tabelaDespesas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header do painel - usando mesmo estilo da tela de receitas
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(231, 76, 60)); // Cor vermelha para despesas
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("💸 Gestão de Despesas e Gastos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Registre suas despesas e gastos de forma organizada");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(189, 195, 199));

        JPanel painelTextos = new JPanel(new BorderLayout());
        painelTextos.setBackground(new Color(231, 76, 60));
        painelTextos.add(lblTitulo, BorderLayout.NORTH);
        painelTextos.add(lblSubtitulo, BorderLayout.SOUTH);

        header.add(painelTextos, BorderLayout.WEST);

        // Painel principal do formulário
        JPanel painelFormulario = createFormularioMelhorado();

        // Painel da tabela
        JPanel painelTabela = createTabelaMelhorada();

        // Dividir em duas partes: formulário (40%) e tabela (60%)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelFormulario, painelTabela);
        splitPane.setDividerLocation(400);
        splitPane.setOneTouchExpandable(true);

        add(header, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormularioMelhorado() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Seção de tipo de despesa
        JPanel painelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTipo.setBackground(Color.WHITE);
        painelTipo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
            "Tipo de Despesa"
        ));

        JRadioButton rbDespesaGeral = new JRadioButton("Despesa Geral", true);
        JRadioButton rbDespesaFixa = new JRadioButton("Despesa Fixa/Recorrente");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbDespesaGeral);
        grupo.add(rbDespesaFixa);

        painelTipo.add(rbDespesaGeral);
        painelTipo.add(rbDespesaFixa);

        // Formulário principal
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Descrição
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formulario.add(txtDescricao, gbc);

        // Valor
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formulario.add(txtValor, gbc);

        // Categoria
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formulario.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formulario.add(cmbCategoria, gbc);

        // Data
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblData = new JLabel("Data:");
        formulario.add(lblData, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formulario.add(txtData, gbc);

        // Observações
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        formulario.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JScrollPane scrollObs = new JScrollPane(txtObservacoes);
        scrollObs.setPreferredSize(new Dimension(0, 80));
        formulario.add(scrollObs, gbc);

        // Botões
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(Color.WHITE);

        // Atualizar cores dos botões para o tema de despesas
        btnSalvar.setBackground(new Color(231, 76, 60)); // Vermelho para despesas
        btnSalvar.setForeground(Color.WHITE);
        btnLimpar.setBackground(new Color(149, 165, 166));
        btnLimpar.setForeground(Color.WHITE);
        btnExcluir.setBackground(new Color(192, 57, 43));
        btnExcluir.setForeground(Color.WHITE);

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnExcluir);
        formulario.add(painelBotoes, gbc);

        painel.add(painelTipo);
        painel.add(Box.createRigidArea(new Dimension(0, 10)));
        painel.add(formulario);

        return painel;
    }

    private JPanel createTabelaMelhorada() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header da tabela
        JLabel lblTitulo = new JLabel("📋 Despesas Cadastradas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Configurar tabela
        tabelaDespesas = new JTable(modeloTabela);
        tabelaDespesas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaDespesas.setRowHeight(30);
        tabelaDespesas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelaDespesas.getTableHeader().setBackground(new Color(231, 76, 60)); // Vermelho para despesas
        tabelaDespesas.getTableHeader().setForeground(Color.WHITE);

        // Configurar larguras das colunas
        tabelaDespesas.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabelaDespesas.getColumnModel().getColumn(1).setPreferredWidth(200); // Descrição
        tabelaDespesas.getColumnModel().getColumn(2).setPreferredWidth(120); // Categoria
        tabelaDespesas.getColumnModel().getColumn(3).setPreferredWidth(100); // Valor
        tabelaDespesas.getColumnModel().getColumn(4).setPreferredWidth(100); // Data
        tabelaDespesas.getColumnModel().getColumn(5).setPreferredWidth(150); // Observações

        JScrollPane scrollPane = new JScrollPane(tabelaDespesas);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }
    
    private void setupEventos() {
        btnSalvar.addActionListener(e -> salvarDespesa());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluirDespesa());

        tabelaDespesas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarDespesaSelecionada();
            }
        });
    }
    
    private void carregarCategorias() {
        try {
            List<Categoria> categorias = categoriaController.listarCategorias();
            cmbCategoria.removeAllItems();
            for (Categoria categoria : categorias) {
                cmbCategoria.addItem(categoria);
            }
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDespesas() {
        try {
            List<Transacao> despesas = transacaoController.listarTransacoesPorTipo(TipoTransacao.DESPESA);
            modeloTabela.setRowCount(0);

            for (Transacao despesa : despesas) {
                Object[] row = {
                    despesa.getId(),
                    despesa.getDescricao(),
                    despesa.getCategoria() != null ? despesa.getCategoria().getNome() : "N/A",
                    FormatUtils.formatarValor(despesa.getValor()),
                    despesa.getDataTransacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    despesa.getObservacao()
                };
                modeloTabela.addRow(row);
            }
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar despesas: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void salvarDespesa() {
        try {
            // Validar campos
            if (txtDescricao.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Descrição é obrigatória!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal valor = new BigDecimal(txtValor.getText().replace(",", "."));
            LocalDate data = LocalDate.parse(txtData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();

            Transacao despesa = new Transacao();
            despesa.setDescricao(txtDescricao.getText().trim());
            despesa.setValor(valor);
            despesa.setDataTransacao(data);
            despesa.setTipo(TipoTransacao.DESPESA);
            despesa.setCategoriaId(categoria != null ? categoria.getId() : null);
            despesa.setObservacao(txtObservacoes.getText().trim());

            // Associar automaticamente ao usuário logado
            Long idUsuarioLogado = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();
            if (idUsuarioLogado != null) {
                despesa.setUsuarioId(idUsuarioLogado);
            } else {
                JOptionPane.showMessageDialog(this, "Erro: Nenhum usuário logado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            transacaoController.salvarTransacao(despesa);

            JOptionPane.showMessageDialog(this, "Despesa salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarDespesas();

            // Atualizar apenas o resumo rápido sem navegar para o dashboard
            if (telaPrincipal != null) {
                telaPrincipal.atualizarResumoRapido();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida! Use o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar despesa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparFormulario() {
        txtDescricao.setText("");
        txtValor.setText("");
        txtData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtObservacoes.setText("");
        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }
        tabelaDespesas.clearSelection();
    }
    
    private void excluirDespesa() {
        int selectedRow = tabelaDespesas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma despesa para excluir!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir esta despesa?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) modeloTabela.getValueAt(selectedRow, 0);
                transacaoController.excluirTransacao(id);

                JOptionPane.showMessageDialog(this, "Despesa excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarDespesas();

                // Atualizar apenas o resumo rápido sem navegar para o dashboard
                if (telaPrincipal != null) {
                    telaPrincipal.atualizarResumoRapido();
                }

            } catch (BusinessException e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir despesa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarDespesaSelecionada() {
        int selectedRow = tabelaDespesas.getSelectedRow();
        if (selectedRow != -1) {
            txtDescricao.setText((String) modeloTabela.getValueAt(selectedRow, 1));
            String valorStr = (String) modeloTabela.getValueAt(selectedRow, 3);
            txtValor.setText(valorStr.replace("R$ ", "").replace(".", "").replace(",", "."));
            txtData.setText((String) modeloTabela.getValueAt(selectedRow, 4));
            txtObservacoes.setText((String) modeloTabela.getValueAt(selectedRow, 5));

            // Selecionar categoria correspondente
            String nomeCategoria = (String) modeloTabela.getValueAt(selectedRow, 2);
            for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                Categoria cat = (Categoria) cmbCategoria.getItemAt(i);
                if (cat.getNome().equals(nomeCategoria)) {
                    cmbCategoria.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}
