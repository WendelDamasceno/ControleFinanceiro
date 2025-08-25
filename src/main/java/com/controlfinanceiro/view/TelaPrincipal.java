package com.controlfinanceiro.view;

import com.controlfinanceiro.controller.TransacaoController;
import com.controlfinanceiro.controller.CategoriaController;
import com.controlfinanceiro.controller.RelatorioController;
import com.controlfinanceiro.controller.DashboardController;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.enums.TipoTransacao;
import com.controlfinanceiro.util.FormatUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TelaPrincipal extends JFrame {
    private JPanel painelConteudo;
    private CardLayout cardLayout;
    
    // Controllers
    private final TransacaoController transacaoController;
    private final CategoriaController categoriaController;
    private final RelatorioController relatorioController;
    private final DashboardController dashboardController;

    // Painéis das diferentes telas
    private CadastroReceita cadastroReceita;
    private CadastroDespesa cadastroDespesa;
    private GerarRelatorio gerarRelatorio;
    
    // Botões do menu para controle visual
    private JButton btnDashboard;
    private JButton btnReceita;
    private JButton btnDespesa;
    private JButton btnRelatorio;

    // Paleta de cores refatorada - Azul moderno e sóbrio alinhado com login
    private static final Color COR_PRIMARIA = new Color(56, 112, 232);       // Azul primário vibrante #3870E8
    private static final Color COR_PRIMARIA_HOVER = new Color(31, 91, 219);  // Azul hover vibrante #1F5BDB
    private static final Color COR_DESTAQUE = new Color(30, 64, 175);        // Azul destaque profundo #1E40AF
    private static final Color COR_SUCESSO = new Color(34, 197, 94);         // Verde sucesso moderno #22C55E
    private static final Color COR_ERRO = new Color(239, 68, 68);            // Vermelho erro moderno #EF4444
    private static final Color COR_GRADIENTE_INICIO = new Color(249, 250, 251); // Fundo claro #F9FAFB
    private static final Color COR_GRADIENTE_FIM = new Color(243, 244, 246);  // Fundo médio #F3F4F6
    private static final Color COR_CARD = Color.WHITE;                        // Branco #FFFFFF
    private static final Color COR_TEXTO_PRIMARIO = new Color(17, 24, 39);    // Texto escuro #111827
    private static final Color COR_TEXTO_SECUNDARIO = new Color(55, 65, 81);  // Texto médio #374151
    private static final Color COR_TEXTO_TERCIARIO = new Color(107, 114, 128); // Texto claro #6B7280
    private static final Color COR_TEXTO_PLACEHOLDER = new Color(156, 163, 175); // Placeholder #9CA3AF
    private static final Color COR_BORDA = new Color(229, 231, 235);          // Borda clara #E5E7EB
    private static final Color COR_BORDA_FOCO = COR_DESTAQUE;                 // Borda de foco
    private static final Color COR_BORDA_BOTAO = new Color(209, 213, 219);    // Borda de botão #D1D5DB

    // Cores para o painel lateral e header - Tom azul moderno e sóbrio
    private static final Color COR_FUNDO_LATERAL = new Color(248, 250, 252);  // Fundo cinza muito claro
    private static final Color COR_MENU_LATERAL = new Color(30, 58, 138);     // Azul escuro moderno #1E3A8A
    private static final Color COR_HEADER = new Color(37, 99, 235);           // Azul header moderno #2563EB
    private static final Color COR_TEXTO_LATERAL = Color.WHITE;               // Texto branco
    private static final Color COR_TEXTO_DESTACADO = new Color(219, 234, 254); // Texto destacado azul claro #DBEAFE

    // Cores adicionais para botões e estados
    private static final Color COR_MENU_ITEM_ATIVO = new Color(59, 130, 246); // Azul ativo #3B82F6
    private static final Color COR_MENU_ITEM_HOVER = new Color(55, 65, 81);   // Cinza hover #374151
    private static final Color COR_MENU_ITEM_INATIVO = new Color(75, 85, 99); // Cinza inativo #4B5563

    // Referência ao resumo rápido para atualização
    private JPanel resumoRapidoAtual;

    public TelaPrincipal() {
        this.transacaoController = new TransacaoController();
        this.categoriaController = new CategoriaController();
        this.relatorioController = new RelatorioController();
        this.dashboardController = new DashboardController();

        initComponents();
        setupLayout();
        setupEventos();
    }

    private void initComponents() {
        setTitle("Controle de Receitas e Despesas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Inicializar painéis
        cardLayout = new CardLayout();
        painelConteudo = new JPanel(cardLayout);
        
        cadastroReceita = new CadastroReceita(transacaoController, categoriaController);
        cadastroDespesa = new CadastroDespesa(transacaoController, categoriaController);
        gerarRelatorio = new GerarRelatorio(relatorioController);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior com título melhorado
        JPanel painelTitulo = createHeaderModerno();

        // Painel lateral com menu melhorado
        JPanel painelMenu = createMenuLateralMelhorado();

        // Criar dashboard inicial e nomear corretamente
        JPanel dashboardInicial = createDashboard();
        dashboardInicial.setName("DASHBOARD");

        // Nomear os painéis corretamente para a navegação funcionar
        cadastroReceita.setName("RECEITA");
        cadastroDespesa.setName("DESPESA");
        gerarRelatorio.setName("RELATORIO");

        // Configurar referências para atualização automática do dashboard
        cadastroReceita.setTelaPrincipal(this);
        cadastroDespesa.setTelaPrincipal(this);

        // Adicionar painéis ao CardLayout
        painelConteudo.add(dashboardInicial, "DASHBOARD");
        painelConteudo.add(cadastroReceita, "RECEITA");
        painelConteudo.add(cadastroDespesa, "DESPESA");
        painelConteudo.add(gerarRelatorio, "RELATORIO");
        
        // Adicionar componentes à janela com melhor layout
        add(painelTitulo, BorderLayout.NORTH);
        add(painelMenu, BorderLayout.WEST);
        add(painelConteudo, BorderLayout.CENTER);
        
        // Mostrar dashboard inicialmente
        cardLayout.show(painelConteudo, "DASHBOARD");
    }
    
    private JPanel createHeaderModerno() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_HEADER);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, COR_DESTAQUE),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // Título à esquerda
        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painelTitulo.setBackground(COR_HEADER);

        JLabel lblIcone = new JLabel("💰");
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcone.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        JLabel lblTitulo = new JLabel("Controle Financeiro");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COR_TEXTO_LATERAL);

        painelTitulo.add(lblIcone);
        painelTitulo.add(lblTitulo);

        // Informações do usuário à direita
        JPanel painelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelUsuario.setBackground(COR_HEADER);

        // Obter nome do usuário logado
        String nomeUsuario = com.controlfinanceiro.util.SessaoUsuario.getInstance().getNomeUsuarioLogado();

        JLabel lblUsuario = new JLabel("👤 " + nomeUsuario);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUsuario.setForeground(COR_TEXTO_DESTACADO);
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));

        painelUsuario.add(lblUsuario);

        header.add(painelTitulo, BorderLayout.WEST);
        header.add(painelUsuario, BorderLayout.EAST);

        return header;
    }

    private JPanel createMenuLateralMelhorado() {
        JPanel painelMenu = new JPanel();
        painelMenu.setLayout(new BorderLayout());
        painelMenu.setBackground(COR_MENU_LATERAL);
        painelMenu.setPreferredSize(new Dimension(260, 0));
        painelMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COR_BORDA));

        // Seção superior do menu
        JPanel menuSuperior = createMenuSuperior();

        // Seção principal do menu
        JPanel menuPrincipal = createMenuPrincipal();

        // Seção inferior do menu
        JPanel menuInferior = createMenuInferior();

        painelMenu.add(menuSuperior, BorderLayout.NORTH);
        painelMenu.add(menuPrincipal, BorderLayout.CENTER);
        painelMenu.add(menuInferior, BorderLayout.SOUTH);

        return painelMenu;
    }
    
    private JPanel createMenuSuperior() {
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.setBackground(COR_MENU_LATERAL);
        superior.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

        // Título da seção
        JLabel lblSecao = new JLabel("NAVEGAÇÃO PRINCIPAL");
        lblSecao.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSecao.setForeground(COR_TEXTO_DESTACADO);
        lblSecao.setAlignmentX(Component.LEFT_ALIGNMENT);

        superior.add(lblSecao);
        superior.add(Box.createRigidArea(new Dimension(0, 15)));

        return superior;
    }

    private JPanel createMenuPrincipal() {
        JPanel principal = new JPanel();
        principal.setLayout(new BoxLayout(principal, BoxLayout.Y_AXIS));
        principal.setBackground(COR_MENU_LATERAL);
        principal.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Botões do menu principal
        btnDashboard = createMenuButtonMelhorado("🏠", "Dashboard", "Visão geral das finanças", "DASHBOARD", true);
        btnReceita = createMenuButtonMelhorado("💰", "Receitas", "Cadastrar receitas e salários", "RECEITA", false);
        btnDespesa = createMenuButtonMelhorado("💸", "Despesas", "Registrar gastos", "DESPESA", false);
        btnRelatorio = createMenuButtonMelhorado("📊", "Relatórios", "Análises e gráficos", "RELATORIO", false);

        principal.add(btnDashboard);
        principal.add(Box.createRigidArea(new Dimension(0, 8)));
        principal.add(btnReceita);
        principal.add(Box.createRigidArea(new Dimension(0, 8)));
        principal.add(btnDespesa);
        principal.add(Box.createRigidArea(new Dimension(0, 8)));
        principal.add(btnRelatorio);

        return principal;
    }

    private JPanel createMenuInferior() {
        JPanel inferior = new JPanel();
        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));
        inferior.setBackground(COR_MENU_LATERAL);
        inferior.setBorder(BorderFactory.createEmptyBorder(20, 20, 25, 20));

        // Separador
        JSeparator separador = new JSeparator();
        separador.setForeground(COR_TEXTO_DESTACADO);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Resumo rápido
        resumoRapidoAtual = createResumoRapidoMenu();

        // Botão de logout
        JButton btnLogout = createLogoutButton();

        // Versão do sistema
        JLabel lblVersao = new JLabel("POO - IFBA");
        lblVersao.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersao.setForeground(COR_TEXTO_DESTACADO);
        lblVersao.setAlignmentX(Component.CENTER_ALIGNMENT);

        inferior.add(separador);
        inferior.add(Box.createRigidArea(new Dimension(0, 15)));
        inferior.add(resumoRapidoAtual);
        inferior.add(Box.createRigidArea(new Dimension(0, 15)));
        inferior.add(btnLogout);
        inferior.add(Box.createRigidArea(new Dimension(0, 10)));
        inferior.add(lblVersao);

        return inferior;
    }

    private JButton createLogoutButton() {
        JButton btnLogout = new JButton();
        btnLogout.setLayout(new BorderLayout());
        btnLogout.setPreferredSize(new Dimension(220, 45));
        btnLogout.setMaximumSize(new Dimension(220, 45)); // Usar tamanho fixo em vez de Integer.MAX_VALUE
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT); // Centralizar o botão
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(231, 76, 60), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Painel com ícone e texto centralizado
        JPanel painelConteudo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelConteudo.setOpaque(false);

        JLabel lblIcone = new JLabel("🚪");
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel lblTexto = new JLabel("Sair");
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTexto.setForeground(Color.WHITE);

        painelConteudo.add(lblIcone);
        painelConteudo.add(lblTexto);

        btnLogout.add(painelConteudo, BorderLayout.CENTER);

        // Evento de logout
        btnLogout.addActionListener(e -> realizarLogout());

        // Efeitos hover
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(231, 76, 60));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(192, 57, 43));
            }
        });

        return btnLogout;
    }

    private void realizarLogout() {
        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja sair do sistema?",
            "Confirmar Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            // Limpar a sessão do usuário
            com.controlfinanceiro.util.SessaoUsuario.getInstance().logout();

            dispose(); // Fechar a tela principal
            SwingUtilities.invokeLater(() -> {
                new TelaLogin().setVisible(true); // Abrir tela de login
            });
        }
    }

    private JPanel createResumoRapidoMenu() {
        JPanel resumo = new JPanel();
        resumo.setLayout(new BoxLayout(resumo, BoxLayout.Y_AXIS));
        resumo.setBackground(COR_FUNDO_LATERAL);
        resumo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblTitulo = new JLabel("RESUMO RÁPIDO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitulo.setForeground(COR_TEXTO_TERCIARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            // Usar dados do DashboardController para estatísticas do usuário
            DashboardController.EstatisticasDashboard statsMes = dashboardController.getEstatisticasMesAtual();

            JLabel lblSaldo = new JLabel("Saldo: " + FormatUtils.formatarValor(statsMes.getSaldo()));
            lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblSaldo.setForeground(statsMes.getSaldo().compareTo(BigDecimal.ZERO) >= 0 ? COR_SUCESSO : COR_ERRO);
            lblSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblTransacoes = new JLabel(statsMes.getTotalTransacoes() + " transações este mês");
            lblTransacoes.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblTransacoes.setForeground(COR_TEXTO_TERCIARIO);
            lblTransacoes.setAlignmentX(Component.CENTER_ALIGNMENT);

            resumo.add(lblTitulo);
            resumo.add(Box.createRigidArea(new Dimension(0, 8)));
            resumo.add(lblSaldo);
            resumo.add(Box.createRigidArea(new Dimension(0, 4)));
            resumo.add(lblTransacoes);

        } catch (Exception e) {
            JLabel lblErro = new JLabel("Dados indisponíveis");
            lblErro.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblErro.setForeground(COR_TEXTO_TERCIARIO);
            lblErro.setAlignmentX(Component.CENTER_ALIGNMENT);

            resumo.add(lblTitulo);
            resumo.add(Box.createRigidArea(new Dimension(0, 8)));
            resumo.add(lblErro);
        }

        return resumo;
    }

    private JButton createMenuButtonMelhorado(String icone, String titulo, String descricao, String comando, boolean ativo) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setActionCommand(comando);
        btn.setPreferredSize(new Dimension(220, 60));
        btn.setMaximumSize(new Dimension(220, 60));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Cores baseadas na nova paleta azul moderna
        Color corFundo = ativo ? COR_MENU_ITEM_ATIVO : COR_MENU_LATERAL;
        Color corTexto = ativo ? Color.WHITE : COR_TEXTO_DESTACADO;
        Color corDescricao = ativo ? new Color(219, 234, 254) : new Color(156, 163, 175);

        btn.setBackground(corFundo);

        // Painel do ícone
        JPanel painelIcone = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelIcone.setBackground(corFundo);
        painelIcone.setPreferredSize(new Dimension(50, 60));

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblIcone.setHorizontalAlignment(SwingConstants.CENTER);
        painelIcone.add(lblIcone);

        // Painel do texto
        JPanel painelTexto = new JPanel();
        painelTexto.setLayout(new BoxLayout(painelTexto, BoxLayout.Y_AXIS));
        painelTexto.setBackground(corFundo);
        painelTexto.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(corTexto);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDescricao = new JLabel(descricao);
        lblDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblDescricao.setForeground(corDescricao);
        lblDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelTexto.add(lblTitulo);
        painelTexto.add(Box.createRigidArea(new Dimension(0, 2)));
        painelTexto.add(lblDescricao);

        btn.add(painelIcone, BorderLayout.WEST);
        btn.add(painelTexto, BorderLayout.CENTER);

        // Evento de clique - SIMPLIFIED: Navegação mais simples e robusta
        btn.addActionListener(e -> {
            String cmd = btn.getActionCommand();
            System.out.println("DEBUG: Navegando para: " + cmd);

            try {
                // Navegação simplificada - sempre tenta mostrar o painel
                if ("DASHBOARD".equals(cmd)) {
                    atualizarDashboard();
                } else {
                    cardLayout.show(painelConteudo, cmd);
                    painelConteudo.revalidate();
                    painelConteudo.repaint();
                }

                // Atualizar estado visual dos botões
                atualizarEstadoBotoes(cmd);

                System.out.println("DEBUG: Navegação para " + cmd + " realizada com sucesso");

            } catch (Exception ex) {
                System.err.println("Erro na navegação: " + ex.getMessage());
                ex.printStackTrace();
                // Em caso de erro, tentar navegação simples
                cardLayout.show(painelConteudo, cmd);
            }
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!ativo) {
                    Color corHover = COR_MENU_ITEM_HOVER;
                    btn.setBackground(corHover);
                    painelIcone.setBackground(corHover);
                    painelTexto.setBackground(corHover);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!ativo) {
                    btn.setBackground(corFundo);
                    painelIcone.setBackground(corFundo);
                    painelTexto.setBackground(corFundo);
                }
            }
        });
        
        return btn;
    }
    
    private void atualizarEstadoBotoes(String comandoAtivo) {
        // Resetar todos os botões para estado inativo
        JButton[] botoes = {btnDashboard, btnReceita, btnDespesa, btnRelatorio};

        for (JButton btn : botoes) {
            if (btn != null) {
                // Cores para estado inativo usando a nova paleta azul moderna
                Color corFundo = COR_MENU_LATERAL;
                Color corTexto = COR_TEXTO_DESTACADO;
                Color corDescricao = new Color(156, 163, 175);

                btn.setBackground(corFundo);

                // Atualizar painéis internos
                Component[] components = btn.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        comp.setBackground(corFundo);

                        // Atualizar sub-componentes
                        if (comp instanceof JPanel) {
                            JPanel painel = (JPanel) comp;
                            Component[] subComponents = painel.getComponents();
                            for (Component subComp : subComponents) {
                                if (subComp instanceof JPanel) {
                                    subComp.setBackground(corFundo);

                                    // Atualizar labels no painel de texto
                                    if (subComp instanceof JPanel) {
                                        JPanel subPainel = (JPanel) subComp;
                                        Component[] labels = subPainel.getComponents();
                                        for (int i = 0; i < labels.length; i++) {
                                            if (labels[i] instanceof JLabel) {
                                                JLabel lbl = (JLabel) labels[i];
                                                if (i == 0) { // Título
                                                    lbl.setForeground(corTexto);
                                                } else if (i == 2) { // Descrição (índice 2 por causa do Box.createRigidArea)
                                                    lbl.setForeground(corDescricao);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ativar o botão correspondente ao comando ativo
        JButton botaoAtivo = null;
        switch (comandoAtivo) {
            case "DASHBOARD":
                botaoAtivo = btnDashboard;
                break;
            case "RECEITA":
                botaoAtivo = btnReceita;
                break;
            case "DESPESA":
                botaoAtivo = btnDespesa;
                break;
            case "RELATORIO":
                botaoAtivo = btnRelatorio;
                break;
        }

        if (botaoAtivo != null) {
            // Cores para estado ativo usando a nova paleta azul moderna
            Color corFundoAtivo = COR_MENU_ITEM_ATIVO;
            Color corTextoAtivo = Color.WHITE;
            Color corDescricaoAtiva = new Color(219, 234, 254);

            botaoAtivo.setBackground(corFundoAtivo);

            // Atualizar painéis internos do botão ativo
            Component[] components = botaoAtivo.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    comp.setBackground(corFundoAtivo);

                    // Atualizar sub-componentes
                    if (comp instanceof JPanel) {
                        JPanel painel = (JPanel) comp;
                        Component[] subComponents = painel.getComponents();
                        for (Component subComp : subComponents) {
                            if (subComp instanceof JPanel) {
                                subComp.setBackground(corFundoAtivo);

                                // Atualizar labels no painel de texto
                                if (subComp instanceof JPanel) {
                                    JPanel subPainel = (JPanel) subComp;
                                    Component[] labels = subPainel.getComponents();
                                    for (int i = 0; i < labels.length; i++) {
                                        if (labels[i] instanceof JLabel) {
                                            JLabel lbl = (JLabel) labels[i];
                                            if (i == 0) { // Título
                                                lbl.setForeground(corTextoAtivo);
                                            } else if (i == 2) { // Descrição
                                                lbl.setForeground(corDescricaoAtiva);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Forçar repaint de todos os botões
        for (JButton btn : botoes) {
            if (btn != null) {
                btn.repaint();
            }
        }
    }

    private JPanel createDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COR_GRADIENTE_INICIO, 0, getHeight(), COR_GRADIENTE_FIM);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        dashboard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header melhorado com data e hora
        JPanel painelHeader = createDashboardHeader();

        // Cards de resumo financeiro principais
        JPanel painelResumo = createResumoFinanceiroMelhorado();

        // Painel central com gráficos e informações
        JPanel painelCentral = createPainelCentral();

        dashboard.add(painelHeader, BorderLayout.NORTH);
        dashboard.add(painelResumo, BorderLayout.CENTER);
        dashboard.add(painelCentral, BorderLayout.SOUTH);

        return dashboard;
    }
    
    private JPanel createDashboardHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_GRADIENTE_INICIO);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Verificar se é usuário novo
        boolean isUsuarioNovo = dashboardController.isUsuarioNovo();

        // Título principal
        JLabel lblTitulo = new JLabel("Dashboard Financeiro");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(COR_TEXTO_PRIMARIO);

        // Data atual
        JLabel lblData = new JLabel(LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", java.util.Locale.of("pt", "BR"))));
        lblData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblData.setForeground(COR_TEXTO_TERCIARIO);

        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBackground(COR_GRADIENTE_INICIO);
        painelTitulo.add(lblTitulo);
        painelTitulo.add(Box.createRigidArea(new Dimension(0, 5)));
        painelTitulo.add(lblData);

        // Adicionar badge para usuário novo
        if (isUsuarioNovo) {
            JLabel lblBadge = new JLabel("✨ Novo usuário - Configure suas finanças!");
            lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblBadge.setForeground(COR_SUCESSO);
            lblBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_SUCESSO, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            lblBadge.setOpaque(true);
            lblBadge.setBackground(new Color(COR_SUCESSO.getRed(), COR_SUCESSO.getGreen(), COR_SUCESSO.getBlue(), 20));

            painelTitulo.add(Box.createRigidArea(new Dimension(0, 10)));
            painelTitulo.add(lblBadge);
        }

        header.add(painelTitulo, BorderLayout.WEST);
        return header;
    }

    private JPanel createResumoFinanceiroMelhorado() {
        JPanel painelResumo = new JPanel(new GridLayout(1, 4, 20, 0));
        painelResumo.setOpaque(false); // Torna o painel transparente
        painelResumo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        try {
            // Usar o novo DashboardController para obter dados do usuário logado
            DashboardController.EstatisticasDashboard statsMes = dashboardController.getEstatisticasMesAtual();
            DashboardController.EstatisticasDashboard statsAno = dashboardController.getEstatisticasAnoAtual();

            // Cards melhorados com cores da paleta moderna
            painelResumo.add(createCardMelhorado("💰", "Receitas do Mês",
                FormatUtils.formatarValor(statsMes.getReceitas()),
                statsMes.getTotalTransacoes() > 0 ?
                    String.format("%.0f%% do total mensal",
                        statsMes.getReceitas().doubleValue() / (statsMes.getReceitas().add(statsMes.getDespesas()).doubleValue() + 0.01) * 100)
                    : "0 transações",
                COR_SUCESSO, COR_SUCESSO));

            painelResumo.add(createCardMelhorado("💸", "Despesas do Mês",
                FormatUtils.formatarValor(statsMes.getDespesas()),
                statsMes.getTotalTransacoes() > 0 ?
                    String.format("%.0f%% do total mensal",
                        statsMes.getDespesas().doubleValue() / (statsMes.getReceitas().add(statsMes.getDespesas()).doubleValue() + 0.01) * 100)
                    : "0 transações",
                COR_ERRO, COR_ERRO));

            Color corSaldo = statsMes.getSaldo().compareTo(BigDecimal.ZERO) >= 0 ? COR_SUCESSO : COR_ERRO;
            String iconeSaldo = statsMes.getSaldo().compareTo(BigDecimal.ZERO) >= 0 ? "📈" : "📉";

            painelResumo.add(createCardMelhorado(iconeSaldo, "Saldo do Mês",
                FormatUtils.formatarValor(statsMes.getSaldo()),
                statsMes.getSaldo().compareTo(BigDecimal.ZERO) >= 0 ? "Superávit" : "Déficit",
                corSaldo, corSaldo));

            painelResumo.add(createCardMelhorado("🏆", "Saldo Total",
                FormatUtils.formatarValor(statsAno.getSaldo()),
                "Patrimônio atual",
                statsAno.getSaldo().compareTo(BigDecimal.ZERO) >= 0 ? COR_PRIMARIA : COR_ERRO,
                statsAno.getSaldo().compareTo(BigDecimal.ZERO) >= 0 ? COR_PRIMARIA : COR_ERRO));

        } catch (Exception e) {
            System.err.println("ERROR: Erro ao calcular resumo financeiro personalizado: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, mostrar cards com valores zerados usando cores modernas
            painelResumo.add(createCardMelhorado("💰", "Receitas", "R$ 0,00", "Sem dados", COR_SUCESSO, COR_SUCESSO));
            painelResumo.add(createCardMelhorado("💸", "Despesas", "R$ 0,00", "Sem dados", COR_ERRO, COR_ERRO));
            painelResumo.add(createCardMelhorado("📊", "Saldo", "R$ 0,00", "Neutro", COR_TEXTO_TERCIARIO, COR_TEXTO_TERCIARIO));
            painelResumo.add(createCardMelhorado("🏆", "Total", "R$ 0,00", "Sem dados", COR_PRIMARIA, COR_PRIMARIA));
        }

        return painelResumo;
    }

    private JPanel createCardMelhorado(String icone, String titulo, String valor, String subtitulo, Color cor, Color corEscura) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Adicionar sombra e efeito hover
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(cor, 2),
                    BorderFactory.createEmptyBorder(19, 19, 19, 19)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
        });

        // Header do card com ícone
        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setBackground(Color.WHITE);

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(new Color(108, 117, 125));

        headerCard.add(lblIcone, BorderLayout.WEST);
        headerCard.add(lblTitulo, BorderLayout.CENTER);

        // Valor principal
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValor.setForeground(cor);
        lblValor.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // Subtítulo
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(134, 142, 150));

        card.add(headerCard, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        card.add(lblSubtitulo, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createPainelCentral() {
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setOpaque(false); // Torna o painel transparente

        // Verificar se é usuário novo para mostrar painel apropriado
        boolean isUsuarioNovo = verificarSeUsuarioEhNovo();

        if (isUsuarioNovo) {
            // Painel especial para usuários novos
            JPanel painelGuiaUsuarioNovo = createGuiaUsuarioNovo();
            painelCentral.add(painelGuiaUsuarioNovo, BorderLayout.CENTER);
        } else {
            // Painel de transações recentes para usuários com dados
            JPanel painelTransacoes = createTransacoesRecentes();
            painelCentral.add(painelTransacoes, BorderLayout.CENTER);
        }

        return painelCentral;
    }

    /**
     * Cria um painel especial de boas-vindas e guia para usuários novos
     */
    private JPanel createGuiaUsuarioNovo() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_SUCESSO, 2),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Header do guia
        JPanel headerGuia = new JPanel();
        headerGuia.setLayout(new BoxLayout(headerGuia, BoxLayout.Y_AXIS));
        headerGuia.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("🎉 Bem-vindo ao seu Dashboard Financeiro!");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COR_SUCESSO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Siga os passos abaixo para começar a controlar suas finanças:");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(COR_TEXTO_TERCIARIO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerGuia.add(lblTitulo);
        headerGuia.add(Box.createRigidArea(new Dimension(0, 8)));
        headerGuia.add(lblSubtitulo);

        // Painel de passos
        JPanel painelPassos = new JPanel(new GridLayout(1, 3, 20, 0));
        painelPassos.setBackground(Color.WHITE);
        painelPassos.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Passo 1: Receitas - usando cor de sucesso moderna
        JPanel passo1 = createCardPasso(
            "1", "💰", "Adicione suas Receitas",
            "Cadastre seu salário, freelances e outras fontes de renda",
            COR_SUCESSO,
            "RECEITA"
        );

        // Passo 2: Despesas - usando cor de erro moderna
        JPanel passo2 = createCardPasso(
            "2", "💸", "Registre suas Despesas",
            "Anote gastos como aluguel, alimentação, transporte e lazer",
            COR_ERRO,
            "DESPESA"
        );

        // Passo 3: Relatórios - usando cor primária moderna
        JPanel passo3 = createCardPasso(
            "3", "📊", "Veja seus Relatórios",
            "Acesse análises detalhadas e gráficos do seu desempenho financeiro",
            COR_PRIMARIA,
            "RELATORIO"
        );

        painelPassos.add(passo1);
        painelPassos.add(passo2);
        painelPassos.add(passo3);

        painel.add(headerGuia, BorderLayout.NORTH);
        painel.add(painelPassos, BorderLayout.CENTER);

        return painel;
    }

    /**
     * Verifica se o usuário logado é novo (sem transações)
     */
    private boolean verificarSeUsuarioEhNovo() {
        return dashboardController.isUsuarioNovo();
    }

    /**
     * Atualiza o dashboard com dados frescos do usuário (método público para ser chamado por outras classes)
     */
    public void atualizarDashboard() {
        try {
            // Encontrar o componente do dashboard atual
            Component[] components = painelConteudo.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i].getName() != null && components[i].getName().equals("DASHBOARD")) {
                    painelConteudo.remove(i);
                    break;
                }
            }

            // Criar novo dashboard com dados atualizados
            JPanel novoDashboard = createDashboard();
            novoDashboard.setName("DASHBOARD");

            // Adicionar o novo dashboard na primeira posição
            painelConteudo.add(novoDashboard, "DASHBOARD", 0);

            // Mostrar o dashboard atualizado
            cardLayout.show(painelConteudo, "DASHBOARD");
            painelConteudo.revalidate();
            painelConteudo.repaint();

        } catch (Exception e) {
            System.err.println("Erro ao atualizar dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Atualiza apenas o resumo rápido do menu lateral sem mudar de tela
     */
    public void atualizarResumoRapido() {
        try {
            if (resumoRapidoAtual != null) {
                // Obter o container pai do resumo rápido
                Container parent = resumoRapidoAtual.getParent();
                if (parent != null) {
                    // Remover o resumo atual
                    parent.remove(resumoRapidoAtual);

                    // Criar novo resumo com dados atualizados
                    resumoRapidoAtual = createResumoRapidoMenu();

                    // Adicionar o novo resumo na mesma posição
                    parent.add(resumoRapidoAtual, 2); // Posição 2 no menu inferior (após separador e espaço)

                    // Forçar atualização visual
                    parent.revalidate();
                    parent.repaint();

                    System.out.println("DEBUG: Resumo rápido atualizado com sucesso!");
                } else {
                    System.err.println("WARN: Parent do resumo rápido é null");
                }
            } else {
                System.err.println("WARN: Referência do resumo rápido é null");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar resumo rápido: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Cria um painel de transações recentes para usuários com dados
     */
    private JPanel createTransacoesRecentes() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Título da seção
        JLabel titulo = new JLabel("📋 Transações Recentes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(52, 73, 94));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Lista de transações
        JPanel listaTransacoes = new JPanel();
        listaTransacoes.setLayout(new BoxLayout(listaTransacoes, BoxLayout.Y_AXIS));
        listaTransacoes.setBackground(Color.WHITE);

        try {
            List<Transacao> ultimasTransacoes = dashboardController.getUltimasTransacoes(5);

            if (ultimasTransacoes.isEmpty()) {
                JLabel lblVazio = new JLabel("Nenhuma transação encontrada");
                lblVazio.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                lblVazio.setForeground(new Color(134, 142, 150));
                lblVazio.setHorizontalAlignment(SwingConstants.CENTER);
                listaTransacoes.add(lblVazio);
            } else {
                for (Transacao transacao : ultimasTransacoes) {
                    listaTransacoes.add(createItemTransacao(transacao));
                    listaTransacoes.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        } catch (Exception e) {
            JLabel lblErro = new JLabel("Erro ao carregar transações");
            lblErro.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblErro.setForeground(new Color(220, 53, 69));
            lblErro.setHorizontalAlignment(SwingConstants.CENTER);
            listaTransacoes.add(lblErro);
        }

        JScrollPane scrollPane = new JScrollPane(listaTransacoes);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(0, 200));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    /**
     * Cria um card de passo para o guia do usuário novo
     */
    private JPanel createCardPasso(String numero, String icone, String titulo, String descricao, Color cor, String comando) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Header com número e ícone
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        JLabel lblNumero = new JLabel(numero);
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNumero.setForeground(cor);
        lblNumero.setPreferredSize(new Dimension(40, 40));
        lblNumero.setHorizontalAlignment(SwingConstants.CENTER);
        lblNumero.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblIcone.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(lblNumero, BorderLayout.WEST);
        header.add(lblIcone, BorderLayout.CENTER);

        // Conteúdo
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(Color.WHITE);
        conteudo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(52, 73, 94));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescricao = new JLabel("<html><div style='text-align: center'>" + descricao + "</div></html>");
        lblDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescricao.setForeground(new Color(108, 117, 125));
        lblDescricao.setAlignmentX(Component.CENTER_ALIGNMENT);

        conteudo.add(lblTitulo);
        conteudo.add(Box.createRigidArea(new Dimension(0, 8)));
        conteudo.add(lblDescricao);

        card.add(header, BorderLayout.NORTH);
        card.add(conteudo, BorderLayout.CENTER);

        // Evento de clique para navegar
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(painelConteudo, comando);
                atualizarEstadoBotoes(comando);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(cor, 2),
                    BorderFactory.createEmptyBorder(19, 19, 19, 19)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(cor, 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
        });

        return card;
    }

    /**
     * Método corrigido para criar itens de transação
     */
    private JPanel createItemTransacao(Transacao transacao) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(new Color(248, 249, 250));
        item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        String icone = transacao.getTipo() == TipoTransacao.RECEITA ? "💰" : "💸";
        Color cor = transacao.getTipo() == TipoTransacao.RECEITA ? new Color(40, 167, 69) : new Color(220, 53, 69);

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JPanel painelInfo = new JPanel(new BorderLayout());
        painelInfo.setBackground(new Color(248, 249, 250));

        JLabel lblDescricao = new JLabel(transacao.getDescricao());
        lblDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDescricao.setForeground(new Color(52, 73, 94));

        JLabel lblData = new JLabel(transacao.getDataTransacao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM")));
        lblData.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblData.setForeground(new Color(134, 142, 150));

        painelInfo.add(lblDescricao, BorderLayout.CENTER);
        painelInfo.add(lblData, BorderLayout.SOUTH);

        JLabel lblValor = new JLabel(FormatUtils.formatarValor(transacao.getValor()));
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValor.setForeground(cor);

        item.add(lblIcone, BorderLayout.WEST);
        item.add(painelInfo, BorderLayout.CENTER);
        item.add(lblValor, BorderLayout.EAST);

        return item;
    }

    /**
     * Configura os eventos da tela
     */
    private void setupEventos() {
        // Os eventos já estão configurados nos botões do menu
        // Este método pode ser usado para configurar eventos globais se necessário
    }
}
