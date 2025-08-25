package com.controlfinanceiro.view;

import com.controlfinanceiro.controller.UsuarioController;
import com.controlfinanceiro.exception.BusinessException;
import com.controlfinanceiro.model.Usuario;
import com.controlfinanceiro.util.SessaoUsuario;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class TelaLogin extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnLogin;
    private JButton btnCadastrar;
    private JLabel lblMensagem;
    private JLabel lblEsqueciSenha;

    private final UsuarioController usuarioController;

    // Paleta de cores moderna e elegante - Versão mais atrativa
    private static final Color COR_PRIMARIA = new Color(56, 112, 232);       // Azul primário mais vibrante
    private static final Color COR_PRIMARIA_HOVER = new Color(31, 91, 219);  // Azul hover mais vibrante
    private static final Color COR_DESTAQUE = new Color(130, 80, 223);       // Roxo/Azul destaque mais vibrante
    private static final Color COR_SUCESSO = new Color(33, 187, 120);        // Verde sucesso mais vibrante
    private static final Color COR_ERRO = new Color(231, 70, 70);            // Vermelho erro mais vibrante
    private static final Color COR_GRADIENTE_INICIO = new Color(249, 250, 251); // Fundo claro #f9fafb
    private static final Color COR_GRADIENTE_FIM = new Color(243, 244, 246);  // Fundo médio #f3f4f6
    private static final Color COR_CARD = Color.WHITE;                        // Branco #ffffff
    private static final Color COR_TEXTO_PRIMARIO = new Color(17, 24, 39);    // Texto escuro #111827
    private static final Color COR_TEXTO_SECUNDARIO = new Color(55, 65, 81);  // Texto médio mais escuro
    private static final Color COR_TEXTO_TERCIARIO = new Color(75, 85, 99);   // Texto claro mais escuro
    private static final Color COR_TEXTO_PLACEHOLDER = new Color(156, 163, 175); // Placeholder #9ca3af
    private static final Color COR_BORDA = new Color(229, 231, 235);          // Borda clara #e5e7eb
    private static final Color COR_BORDA_FOCO = COR_DESTAQUE;                 // Borda de foco
    private static final Color COR_BORDA_BOTAO = new Color(209, 213, 219);    // Borda de botão #d1d5db

    // Cores para o painel lateral - Corrigido para acessibilidade WCAG
    private static final Color COR_FUNDO_LATERAL = new Color(248, 250, 252);  // Fundo cinza muito claro para melhor contraste
    private static final Color COR_TEXTO_LATERAL = new Color(51, 51, 51);     // Texto escuro #333333 para máxima legibilidade
    private static final Color COR_TEXTO_DESTACADO = new Color(79, 70, 229);  // Azul para destaque com bom contraste

    public TelaLogin() {
        this.usuarioController = new UsuarioController();
        initComponents();
        setupLayout();
        setupEventos();
    }

    private void initComponents() {
        setTitle("Minhas Finanças - Controle Financeiro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- Componentes com ícones modernos ---
        txtUsuario = createTextFieldWithIcon("👤", "Nome de usuário");
        txtSenha = createPasswordFieldWithIcon("🔒", "Senha");

        btnLogin = new JButton("Entrar");
        btnCadastrar = new JButton("Criar conta");
        lblMensagem = new JLabel(" ", SwingConstants.CENTER);
        lblEsqueciSenha = new JLabel("Esqueceu sua senha?");

        configurarBotaoPrimario(btnLogin);
        configurarBotaoSecundario(btnCadastrar);
        configurarLink(lblEsqueciSenha);

        lblMensagem.setFont(new Font("Poppins", Font.PLAIN, 14));
    }

    // --- Métodos de configuração de componentes ---

    private void configurarBotaoPrimario(JButton button) {
        button.setFont(new Font("Poppins", Font.BOLD, 15));
        button.setBackground(COR_PRIMARIA);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(COR_PRIMARIA, 12, 0));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(COR_PRIMARIA_HOVER);
                button.setBorder(new RoundedBorder(COR_PRIMARIA_HOVER, 12, 0));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(COR_PRIMARIA);
                button.setBorder(new RoundedBorder(COR_PRIMARIA, 12, 0));
            }
        });
    }

    private void configurarBotaoSecundario(JButton button) {
        button.setFont(new Font("Poppins", Font.PLAIN, 15));
        button.setForeground(COR_TEXTO_PRIMARIO);
        button.setBackground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(COR_BORDA_BOTAO, 12, 1));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(249, 250, 251));
                button.setBorder(new RoundedBorder(COR_DESTAQUE, 12, 1));
                button.setForeground(COR_DESTAQUE);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setBorder(new RoundedBorder(COR_BORDA_BOTAO, 12, 1));
                button.setForeground(COR_TEXTO_PRIMARIO);
            }
        });
    }

    private void configurarLink(JLabel label) {
        label.setFont(new Font("Poppins", Font.PLAIN, 14));
        label.setForeground(COR_TEXTO_TERCIARIO);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                label.setForeground(COR_DESTAQUE);
                label.setText("<html><u>" + label.getText() + "</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                label.setForeground(COR_TEXTO_TERCIARIO);
                label.setText(label.getText().replaceAll("</?u>|</?html>", ""));
            }
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(TelaLogin.this,
                    "Funcionalidade de recuperação de senha ainda não implementada.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // --- Layout da tela ---

    private void setupLayout() {
        // Painel de fundo com gradiente suave
        JPanel painelFundo = new JPanel(new GridBagLayout()) {
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

        // Layout em duas colunas
        setLayout(new GridLayout(1, 2, 0, 0));

        // Coluna esquerda - Imagem ilustrativa
        JPanel painelImagem = createIlustrativePanel();

        // Coluna direita - Formulário de login
        JPanel painelLogin = new JPanel(new GridBagLayout());
        painelLogin.setBackground(COR_CARD);

        JPanel cardLogin = createLoginCard();

        painelLogin.add(cardLogin);

        // Adicionar as duas colunas
        add(painelImagem);
        add(painelLogin);
    }

    private JPanel createIlustrativePanel() {
        JPanel painelImagem = new JPanel(new BorderLayout());
        painelImagem.setBackground(COR_FUNDO_LATERAL); // Cor mais escura para melhor contraste

        // Painel para título e subtítulo
        JPanel painelTexto = new JPanel();
        painelTexto.setLayout(new BoxLayout(painelTexto, BoxLayout.Y_AXIS));
        painelTexto.setBackground(new Color(0, 0, 0, 0)); // Transparente
        painelTexto.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Logo grande
        JLabel lblLogo = new JLabel("💰");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        lblLogo.setForeground(COR_TEXTO_LATERAL);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título principal com fonte maior
        JLabel lblTitulo = new JLabel("Minhas Finanças");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 38)); // Fonte maior
        lblTitulo.setForeground(COR_TEXTO_LATERAL);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo com realce
        JLabel lblSubtitulo = new JLabel("Simplifique sua vida financeira");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 20));
        lblSubtitulo.setForeground(COR_TEXTO_DESTACADO); // Cor com melhor contraste
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Recursos principais com melhor espaçamento e alinhamento
        String[] recursos = {
            "✓ Controle total sobre suas finanças",
            "✓ Análises detalhadas de gastos",
            "✓ Planejamento orçamentário",
            "✓ Relatórios personalizados"
        };

        JPanel painelRecursos = new JPanel();
        painelRecursos.setLayout(new BoxLayout(painelRecursos, BoxLayout.Y_AXIS));
        painelRecursos.setBackground(new Color(0, 0, 0, 0)); // Transparente
        painelRecursos.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        for (String recurso : recursos) {
            JLabel lblRecurso = new JLabel(recurso);
            lblRecurso.setFont(new Font("Poppins", Font.BOLD, 16)); // Fonte em negrito para melhor legibilidade
            lblRecurso.setForeground(COR_TEXTO_LATERAL);
            lblRecurso.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Painel para cada item para garantir melhor alinhamento
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            itemPanel.setBackground(new Color(0, 0, 0, 0)); // Transparente
            itemPanel.add(lblRecurso);

            painelRecursos.add(itemPanel);
            painelRecursos.add(Box.createVerticalStrut(15)); // Espaçamento maior entre itens
        }

        painelTexto.add(Box.createVerticalGlue());
        painelTexto.add(lblLogo);
        painelTexto.add(Box.createVerticalStrut(16));
        painelTexto.add(lblTitulo);
        painelTexto.add(Box.createVerticalStrut(8));
        painelTexto.add(lblSubtitulo);
        painelTexto.add(Box.createVerticalStrut(10));
        painelTexto.add(painelRecursos);
        painelTexto.add(Box.createVerticalGlue());

        painelImagem.add(painelTexto, BorderLayout.CENTER);

        return painelImagem;
    }

    private JPanel createLoginCard() {
        JPanel cardLogin = new JPanel();
        cardLogin.setLayout(new BoxLayout(cardLogin, BoxLayout.Y_AXIS));
        cardLogin.setBackground(COR_CARD);
        cardLogin.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        cardLogin.setMaximumSize(new Dimension(440, 600));
        cardLogin.setPreferredSize(new Dimension(440, 600));

        // === SEÇÃO HEADER ===
        cardLogin.add(createHeader());
        cardLogin.add(Box.createVerticalStrut(40));

        // === SEÇÃO FORMULÁRIO ===
        cardLogin.add(createFormSection());
        cardLogin.add(Box.createVerticalStrut(30));

        // === SEÇÃO AÇÕES PRINCIPAIS ===
        cardLogin.add(createActionSection());
        cardLogin.add(Box.createVerticalStrut(24));

        // === SEPARADOR VISUAL ===
        cardLogin.add(createDivider());
        cardLogin.add(Box.createVerticalStrut(24));

        // === SEÇÃO AÇÕES SECUNDÁRIAS ===
        cardLogin.add(createSecondaryActionSection());

        return cardLogin;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(COR_CARD);

        // Título da seção de login
        JLabel lblTitulo = new JLabel("Acesse sua conta");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
        lblTitulo.setForeground(COR_TEXTO_PRIMARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo mais sutil
        JLabel lblSubtitulo = new JLabel("Entre com seus dados");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 16));
        lblSubtitulo.setForeground(COR_TEXTO_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblTitulo);
        header.add(Box.createVerticalStrut(8));
        header.add(lblSubtitulo);

        return header;
    }

    private JPanel createFormSection() {
        JPanel formSection = new JPanel();
        formSection.setLayout(new BoxLayout(formSection, BoxLayout.Y_AXIS));
        formSection.setBackground(COR_CARD);

        // Campo Usuário
        JLabel lblUsuario = new JLabel("Nome de usuário");
        lblUsuario.setFont(new Font("Poppins", Font.PLAIN, 15));
        lblUsuario.setForeground(COR_TEXTO_PRIMARIO);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        formSection.add(lblUsuario);
        formSection.add(Box.createVerticalStrut(8));
        formSection.add(txtUsuario);
        formSection.add(Box.createVerticalStrut(20));

        // Campo Senha
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setFont(new Font("Poppins", Font.PLAIN, 15));
        lblSenha.setForeground(COR_TEXTO_PRIMARIO);
        lblSenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        // Link "Esqueci minha senha" centralizado
        JPanel linkContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        linkContainer.setBackground(COR_CARD);
        linkContainer.add(lblEsqueciSenha);
        linkContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        formSection.add(lblSenha);
        formSection.add(Box.createVerticalStrut(8));
        formSection.add(txtSenha);
        formSection.add(Box.createVerticalStrut(10));
        formSection.add(linkContainer);

        return formSection;
    }

    private JPanel createActionSection() {
        JPanel actionSection = new JPanel();
        actionSection.setLayout(new BoxLayout(actionSection, BoxLayout.Y_AXIS));
        actionSection.setBackground(COR_CARD);

        // Botão de login centralizado
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        // Mensagem de status centralizada
        lblMensagem.setAlignmentX(Component.CENTER_ALIGNMENT);

        actionSection.add(btnLogin);
        actionSection.add(Box.createVerticalStrut(12));
        actionSection.add(lblMensagem);

        return actionSection;
    }

    private JPanel createDivider() {
        JPanel dividerPanel = new JPanel();
        dividerPanel.setLayout(new BoxLayout(dividerPanel, BoxLayout.X_AXIS));
        dividerPanel.setBackground(COR_CARD);
        dividerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        // Linha à esquerda
        JSeparator leftSeparator = new JSeparator();
        leftSeparator.setForeground(COR_BORDA);
        leftSeparator.setBackground(COR_BORDA);

        // Texto "ou"
        JLabel lblOu = new JLabel(" ou ");
        lblOu.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblOu.setForeground(COR_TEXTO_TERCIARIO);

        // Linha à direita
        JSeparator rightSeparator = new JSeparator();
        rightSeparator.setForeground(COR_BORDA);
        rightSeparator.setBackground(COR_BORDA);

        dividerPanel.add(leftSeparator);
        dividerPanel.add(lblOu);
        dividerPanel.add(rightSeparator);

        return dividerPanel;
    }

    private JPanel createSecondaryActionSection() {
        JPanel secondarySection = new JPanel();
        secondarySection.setLayout(new BoxLayout(secondarySection, BoxLayout.Y_AXIS));
        secondarySection.setBackground(COR_CARD);

        // Texto explicativo centralizado
        JLabel lblTexto = new JLabel("Ainda não tem uma conta?");
        lblTexto.setFont(new Font("Poppins", Font.PLAIN, 15));
        lblTexto.setForeground(COR_TEXTO_SECUNDARIO);
        lblTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botão de cadastro centralizado
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        secondarySection.add(Box.createVerticalStrut(8));
        secondarySection.add(lblTexto);
        secondarySection.add(Box.createVerticalStrut(12));
        secondarySection.add(btnCadastrar);

        return secondarySection;
    }

    // --- Métodos de eventos e lógica ---

    private void setupEventos() {
        btnLogin.addActionListener(e -> realizarLogin());
        btnCadastrar.addActionListener(e -> abrirTelaCadastro());

        KeyAdapter enterPressionado = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.getSource() == txtUsuario) {
                        txtSenha.requestFocusInWindow();
                    } else {
                        realizarLogin();
                    }
                }
            }
        };
        txtUsuario.addKeyListener(enterPressionado);
        txtSenha.addKeyListener(enterPressionado);
    }

    // --- Classes e métodos auxiliares para o design ---

    private JTextField createTextFieldWithIcon(String icon, String placeholder) {
        JTextField textField = new JTextField(placeholder);
        return (JTextField) setupFieldWithIcon(textField, icon, placeholder);
    }

    private JPasswordField createPasswordFieldWithIcon(String icon, String placeholder) {
        JPasswordField passwordField = new JPasswordField(placeholder);
        passwordField.setEchoChar('●'); // Oculta a senha
        return (JPasswordField) setupFieldWithIcon(passwordField, icon, placeholder);
    }

    private JComponent setupFieldWithIcon(JComponent field, String icon, String placeholder) {
        field.setFont(new Font("Poppins", Font.PLAIN, 15));
        field.setForeground(COR_TEXTO_PLACEHOLDER);
        field.setBackground(new Color(250, 251, 252)); // Fundo levemente cinza para melhor contraste

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 251, 252)); // Mesmo fundo levemente cinza
        panel.setBorder(new RoundedBorder(new Color(203, 213, 225), 12, 2)); // Borda mais escura e mais espessa
        panel.add(lblIcon, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        field.setBorder(BorderFactory.createEmptyBorder(16, 5, 16, 15));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                panel.setBorder(new RoundedBorder(COR_BORDA_FOCO, 12, 2));
                panel.setBackground(Color.WHITE); // Fundo branco quando em foco
                field.setBackground(Color.WHITE);
                if (field instanceof JTextField && ((JTextField) field).getText().equals(placeholder)) {
                    ((JTextField) field).setText("");
                    ((JTextField) field).setForeground(COR_TEXTO_PRIMARIO);
                }
                if (field instanceof JPasswordField) {
                    JPasswordField pf = (JPasswordField) field;
                    if (String.valueOf(pf.getPassword()).equals(placeholder)) {
                        pf.setText("");
                        pf.setEchoChar('●');
                        pf.setForeground(COR_TEXTO_PRIMARIO);
                    }
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                panel.setBorder(new RoundedBorder(new Color(203, 213, 225), 12, 2));
                panel.setBackground(new Color(250, 251, 252)); // Volta ao fundo levemente cinza
                field.setBackground(new Color(250, 251, 252));
                if (field instanceof JTextField && ((JTextField) field).getText().isEmpty()) {
                    ((JTextField) field).setText(placeholder);
                    ((JTextField) field).setForeground(COR_TEXTO_PLACEHOLDER);
                }
                if (field instanceof JPasswordField && String.valueOf(((JPasswordField) field).getPassword()).isEmpty()) {
                    JPasswordField pf = (JPasswordField) field;
                    pf.setText(placeholder);
                    pf.setEchoChar((char) 0);
                    pf.setForeground(COR_TEXTO_PLACEHOLDER);
                }
            }
        });
        return field;
    }

    // Classe para bordas arredondadas
    private static class RoundedBorder implements Border {
        private Color color;
        private int radius;
        private int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    // --- Métodos existentes que precisam ser mantidos ---

    private void abrirTelaCadastro() {
        SwingUtilities.invokeLater(() -> {
            TelaCadastroUsuario telaCadastro = new TelaCadastroUsuario(this);

            telaCadastro.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        TelaLogin.this.prepararParaRetornoFoco();

                        Timer returnFocusTimer = new Timer(150, evt -> {
                            if (txtUsuario.getText().trim().isEmpty()) {
                                txtUsuario.requestFocusInWindow();
                            } else {
                                txtSenha.requestFocusInWindow();
                                txtSenha.selectAll();
                            }
                        });
                        returnFocusTimer.setRepeats(false);
                        returnFocusTimer.start();
                    });
                }
            });

            telaCadastro.setVisible(true);
        });
    }

    private void realizarLogin() {
        String nomeUsuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        // Handle placeholder text
        if (nomeUsuario.equals("Nome de usuário")) {
            nomeUsuario = "";
        }
        if (senha.equals("Senha")) {
            senha = "";
        }

        if (nomeUsuario.isEmpty()) {
            exibirMensagem("Por favor, informe o nome do usuário.", COR_ERRO);
            SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());
            return;
        }

        if (senha.isEmpty()) {
            exibirMensagem("Por favor, informe a senha.", COR_ERRO);
            SwingUtilities.invokeLater(() -> txtSenha.requestFocusInWindow());
            return;
        }

        try {
            btnLogin.setEnabled(false);
            btnLogin.setText("Entrando...");
            exibirMensagem("Validando credenciais...", COR_PRIMARIA);

            boolean loginValido = usuarioController.validarLogin(nomeUsuario, senha);

            if (loginValido) {
                Usuario usuario = usuarioController.buscarPorNome(nomeUsuario);

                if (usuario != null) {
                    SessaoUsuario.getInstance().setUsuarioLogado(usuario);

                    exibirMensagem("Login realizado com sucesso!", COR_SUCESSO);

                    SwingUtilities.invokeLater(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        abrirTelaPrincipal();
                    });
                } else {
                    exibirMensagem("Erro ao carregar dados do usuário.", COR_ERRO);
                }
            } else {
                exibirMensagem("Usuário ou senha incorretos.", COR_ERRO);
                limparCampos();
                SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());
            }

        } catch (BusinessException e) {
            exibirMensagem(e.getMessage(), COR_ERRO);
            limparCampos();
            SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());
        } catch (Exception e) {
            exibirMensagem("Erro interno do sistema. Tente novamente.", COR_ERRO);
            System.err.println("Erro no login: " + e.getMessage());
        } finally {
            btnLogin.setEnabled(true);
            btnLogin.setText("Entrar");
        }
    }

    public void exibirMensagemCadastroSucesso() {
        SwingUtilities.invokeLater(() -> {
            toFront();
            setVisible(true);
            requestFocus();

            exibirMensagem("Conta criada com sucesso! Faça login para continuar.", COR_SUCESSO);

            Timer focusTimer = new Timer(200, e -> {
                txtUsuario.requestFocusInWindow();
                txtUsuario.selectAll();
            });
            focusTimer.setRepeats(false);
            focusTimer.start();
        });
    }

    public void prepararParaRetornoFoco() {
        SwingUtilities.invokeLater(() -> {
            setState(Frame.NORMAL);
            toFront();
            requestFocus();
        });
    }

    public void garantirFocoInicial() {
        SwingUtilities.invokeLater(() -> {
            Timer inicialTimer = new Timer(150, e -> {
                if (txtUsuario.getText().trim().isEmpty() || txtUsuario.getText().equals("Nome de usuário")) {
                    txtUsuario.requestFocusInWindow();
                } else {
                    txtSenha.requestFocusInWindow();
                    txtSenha.selectAll();
                }
            });
            inicialTimer.setRepeats(false);
            inicialTimer.start();
        });
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            SwingUtilities.invokeLater(() -> {
                Timer visibilityTimer = new Timer(100, e -> {
                    toFront();
                    requestFocus();
                    if (txtUsuario.getText().trim().isEmpty() || txtUsuario.getText().equals("Nome de usuário")) {
                        txtUsuario.requestFocusInWindow();
                    }
                });
                visibilityTimer.setRepeats(false);
                visibilityTimer.start();
            });
        }
    }

    private void exibirMensagem(String mensagem, Color cor) {
        lblMensagem.setText(mensagem);
        lblMensagem.setForeground(cor);
    }

    private void limparCampos() {
        txtUsuario.setText("Nome de usuário");
        txtUsuario.setForeground(COR_TEXTO_PLACEHOLDER);
        txtSenha.setText("Senha");
        txtSenha.setEchoChar((char) 0);
        txtSenha.setForeground(COR_TEXTO_PLACEHOLDER);
        lblMensagem.setText(" ");
    }

    private void abrirTelaPrincipal() {
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    Class<?> telaPrincipalClass = Class.forName("com.controlfinanceiro.view.TelaPrincipal");
                    Object telaPrincipal = telaPrincipalClass.getDeclaredConstructor().newInstance();

                    java.lang.reflect.Method setVisibleMethod = telaPrincipalClass.getMethod("setVisible", boolean.class);
                    setVisibleMethod.invoke(telaPrincipal, true);

                    this.dispose();

                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(this,
                        "Login realizado com sucesso!\n\nTela principal ainda não implementada.",
                        "Login Bem-sucedido",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    System.err.println("Erro ao abrir tela principal: " + e.getMessage());
                    JOptionPane.showMessageDialog(this,
                        "Login realizado com sucesso, mas houve erro ao abrir a tela principal.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } catch (Exception e) {
            System.err.println("Erro ao tentar abrir tela principal: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Tenta usar o tema Nimbus para melhorar a aparência
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se falhar, usa o tema padrão
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Ignora e usa o look and feel padrão
            }
        }

        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
