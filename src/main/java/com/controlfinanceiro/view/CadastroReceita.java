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
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CadastroReceita extends JPanel {
    private JTextField txtDescricao;
    private JTextField txtValor;
    private JTextField txtData;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<String> cmbMes;
    private JComboBox<String> cmbAno;
    private JCheckBox chkSalario;
    private JTextArea txtObservacoes;
    private JTable tabelaReceitas;
    private DefaultTableModel modeloTabela;
    private JButton btnSalvar;
    private JButton btnLimpar;
    private JButton btnExcluir;

    // Controllers
    private final TransacaoController transacaoController;
    private final CategoriaController categoriaController;

    // Referência à tela principal para atualizar dashboard
    private TelaPrincipal telaPrincipal;

    public CadastroReceita(TransacaoController transacaoController, CategoriaController categoriaController) {
        this.transacaoController = transacaoController;
        this.categoriaController = categoriaController;
        initComponents();
        setupLayout();
        setupEventos();
        carregarCategorias();
        carregarReceitas();
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

        // ComboBox para mês
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                         "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        cmbMes = new JComboBox<>(meses);
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        cmbMes.setVisible(false); // Inicialmente oculto

        // ComboBox para ano
        String[] anos = new String[10];
        int anoAtual = LocalDate.now().getYear();
        for (int i = 0; i < 10; i++) {
            anos[i] = String.valueOf(anoAtual - 5 + i);
        }
        cmbAno = new JComboBox<>(anos);
        cmbAno.setSelectedItem(String.valueOf(anoAtual));
        cmbAno.setVisible(false); // Inicialmente oculto

        chkSalario = new JCheckBox("É Receita de Salário");

        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        txtObservacoes.setBorder(BorderFactory.createLoweredBevelBorder());
        
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar");
        btnExcluir = new JButton("Excluir");

        // Configurar cores dos botões
        btnSalvar.setBackground(new Color(52, 152, 219));
        btnSalvar.setForeground(Color.WHITE);
        btnLimpar.setBackground(new Color(149, 165, 166));
        btnLimpar.setForeground(Color.WHITE);
        btnExcluir.setBackground(new Color(192, 57, 43));
        btnExcluir.setForeground(Color.WHITE);

        // Configurar tabela
        String[] colunas = {"ID", "Descrição", "Categoria", "Valor", "Data", "Observações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaReceitas = new JTable(modeloTabela);
        tabelaReceitas.getColumnModel().getColumn(0).setMaxWidth(50);
        tabelaReceitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header do painel
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("💰 Gestão de Receitas e Salários");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Registre suas receitas e salários de forma organizada");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(189, 195, 199));

        JPanel painelTextos = new JPanel(new BorderLayout());
        painelTextos.setBackground(new Color(52, 73, 94));
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

        // Seção de tipo de receita
        JPanel painelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTipo.setBackground(Color.WHITE);
        painelTipo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Tipo de Receita"
        ));

        JRadioButton rbReceita = new JRadioButton("Receita Geral", true);
        JRadioButton rbSalario = new JRadioButton("Salário Mensal");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbReceita);
        grupo.add(rbSalario);

        painelTipo.add(rbReceita);
        painelTipo.add(rbSalario);

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

        // Data (para receita geral)
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblData = new JLabel("Data:");
        formulario.add(lblData, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formulario.add(txtData, gbc);

        // Mês (para salário)
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblMes = new JLabel("Mês:");
        lblMes.setVisible(false);
        formulario.add(lblMes, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formulario.add(cmbMes, gbc);

        // Ano (para salário)
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblAno = new JLabel("Ano:");
        lblAno.setVisible(false);
        formulario.add(lblAno, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formulario.add(cmbAno, gbc);

        // Observações
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        formulario.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JScrollPane scrollObs = new JScrollPane(txtObservacoes);
        scrollObs.setPreferredSize(new Dimension(0, 80));
        formulario.add(scrollObs, gbc);

        // Botões
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(Color.WHITE);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnExcluir);
        formulario.add(painelBotoes, gbc);

        painel.add(painelTipo);
        painel.add(Box.createRigidArea(new Dimension(0, 10)));
        painel.add(formulario);

        // Configurar eventos dos radio buttons
        rbSalario.addActionListener(e -> {
            boolean isSalario = rbSalario.isSelected();
            txtData.setVisible(!isSalario);
            lblData.setVisible(!isSalario);
            cmbMes.setVisible(isSalario);
            lblMes.setVisible(isSalario);
            cmbAno.setVisible(isSalario);
            lblAno.setVisible(isSalario);

            if (isSalario) {
                txtDescricao.setText("Salário");
            } else {
                txtDescricao.setText("");
            }

            formulario.revalidate();
            formulario.repaint();
        });

        rbReceita.addActionListener(e -> {
            boolean isSalario = rbSalario.isSelected();
            txtData.setVisible(!isSalario);
            lblData.setVisible(!isSalario);
            cmbMes.setVisible(isSalario);
            lblMes.setVisible(isSalario);
            cmbAno.setVisible(isSalario);
            lblAno.setVisible(isSalario);

            if (!isSalario) {
                txtDescricao.setText("");
            }

            formulario.revalidate();
            formulario.repaint();
        });

        return painel;
    }

    private JPanel createTabelaMelhorada() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header da tabela
        JLabel lblTitulo = new JLabel("📋 Receitas Cadastradas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Configurar tabela
        tabelaReceitas = new JTable(modeloTabela);
        tabelaReceitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaReceitas.setRowHeight(30);
        tabelaReceitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelaReceitas.getTableHeader().setBackground(new Color(52, 152, 219));
        tabelaReceitas.getTableHeader().setForeground(Color.WHITE);

        // Configurar larguras das colunas
        tabelaReceitas.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabelaReceitas.getColumnModel().getColumn(1).setPreferredWidth(200); // Descrição
        tabelaReceitas.getColumnModel().getColumn(2).setPreferredWidth(120); // Categoria
        tabelaReceitas.getColumnModel().getColumn(3).setPreferredWidth(100); // Valor
        tabelaReceitas.getColumnModel().getColumn(4).setPreferredWidth(100); // Data
        tabelaReceitas.getColumnModel().getColumn(5).setPreferredWidth(150); // Observações

        JScrollPane scrollPane = new JScrollPane(tabelaReceitas);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    private void setupEventos() {
        btnSalvar.addActionListener(e -> salvarReceita());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluirReceita());

        tabelaReceitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarReceitaSelecionada();
            }
        });

        chkSalario.addItemListener(e -> {
            boolean selecionado = (e.getStateChange() == ItemEvent.SELECTED);
            cmbMes.setVisible(selecionado);
            cmbAno.setVisible(selecionado);
            if (selecionado) {
                txtData.setEnabled(false);
                txtData.setText("");
            } else {
                txtData.setEnabled(true);
                txtData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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

    private void carregarReceitas() {
        try {
            List<Transacao> receitas = transacaoController.listarTransacoesPorTipo(TipoTransacao.RECEITA);
            modeloTabela.setRowCount(0);

            for (Transacao receita : receitas) {
                Object[] row = {
                    receita.getId(),
                    receita.getDescricao(),
                    receita.getCategoria() != null ? receita.getCategoria().getNome() : "N/A",
                    FormatUtils.formatarValor(receita.getValor()),
                    receita.getDataTransacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    receita.getObservacao()
                };
                modeloTabela.addRow(row);
            }
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar receitas: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void salvarReceita() {
        try {
            // Validar campos
            if (txtDescricao.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Descrição é obrigatória!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal valor = new BigDecimal(txtValor.getText().replace(",", "."));
            LocalDate data;

            if (chkSalario.isSelected()) {
                // Para salário, usar o primeiro dia do mês/ano selecionado
                int mes = cmbMes.getSelectedIndex() + 1;
                int ano = Integer.parseInt((String) cmbAno.getSelectedItem());
                data = LocalDate.of(ano, mes, 1);

                // Se não tem descrição específica ou está vazia, definir como "Salário"
                if (txtDescricao.getText().trim().isEmpty()) {
                    txtDescricao.setText("Salário");
                }
            } else {
                data = LocalDate.parse(txtData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();

            Transacao receita = new Transacao();
            receita.setDescricao(txtDescricao.getText().trim());
            receita.setValor(valor);
            receita.setDataTransacao(data);
            receita.setTipo(TipoTransacao.RECEITA);
            receita.setCategoriaId(categoria != null ? categoria.getId() : null);
            receita.setObservacao(txtObservacoes.getText().trim());

            // Associar automaticamente ao usuário logado
            Long idUsuarioLogado = com.controlfinanceiro.util.SessaoUsuario.getInstance().getIdUsuarioLogado();
            if (idUsuarioLogado != null) {
                receita.setUsuarioId(idUsuarioLogado);
            } else {
                JOptionPane.showMessageDialog(this, "Erro: Nenhum usuário logado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            transacaoController.salvarTransacao(receita);

            JOptionPane.showMessageDialog(this, "Receita salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarReceitas();

            // Atualizar apenas o resumo rápido sem navegar para o dashboard
            if (telaPrincipal != null) {
                telaPrincipal.atualizarResumoRapido();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida! Use o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar receita: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
        tabelaReceitas.clearSelection();
        chkSalario.setSelected(false);
        cmbMes.setVisible(false);
        cmbAno.setVisible(false);
        txtData.setEnabled(true);
    }

    private void excluirReceita() {
        int selectedRow = tabelaReceitas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma receita para excluir!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir esta receita?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) modeloTabela.getValueAt(selectedRow, 0);
                transacaoController.excluirTransacao(id);

                JOptionPane.showMessageDialog(this, "Receita excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarReceitas();

                // Atualizar apenas o resumo rápido sem navegar para o dashboard
                if (telaPrincipal != null) {
                    telaPrincipal.atualizarResumoRapido();
                }

            } catch (BusinessException e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir receita: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarReceitaSelecionada() {
        int selectedRow = tabelaReceitas.getSelectedRow();
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
