package com.controlfinanceiro.view;

import com.controlfinanceiro.controller.UsuarioController;
import com.controlfinanceiro.exception.BusinessException;
import com.controlfinanceiro.model.Usuario;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class TelaCadastroUsuario extends JDialog {
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    private JButton btnCadastrar;
    private JButton btnCancelar;
    private JLabel lblMensagem;

    private final UsuarioController usuarioController;
    private final TelaLogin telaLogin;

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

    public TelaCadastroUsuario(TelaLogin telaLogin) {
        super(telaLogin, "Cadastro de Usuário", true);
        this.telaLogin = telaLogin;
        this.usuarioController = new UsuarioController();
        initComponents();
        setupLayout();
        setupEventos();

        // Garantir foco inicial após a tela estar completamente carregada
        SwingUtilities.invokeLater(this::garantirFocoInicial);
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(960, 720);
        setLocationRelativeTo(telaLogin);
        setResizable(false);

        // Inicializar componentes com estilo moderno
        txtUsuario = createTextFieldWithIcon("👤", "Nome de usuário");
        txtSenha = createPasswordFieldWithIcon("🔒", "Senha");
        txtConfirmarSenha = createPasswordFieldWithIcon("🔐", "Confirmar senha");

        btnCadastrar = new JButton("Criar conta");
        btnCancelar = new JButton("Voltar ao login");
        lblMensagem = new JLabel(" ");

        // Configurar botões com estilo moderno
        configurarBotaoPrimario(btnCadastrar);
        configurarBotaoSecundario(btnCancelar);

        // Configurar mensagem
        lblMensagem.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // --- Métodos de configuração de componentes ---

    private void configurarBotaoPrimario(JButton button) {
        button.setFont(new Font("Poppins", Font.BOLD, 15));
        button.setBackground(COR_SUCESSO);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(COR_SUCESSO, 12, 0));
        button.setFocusPainted(false);

        // Efeito hover
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(10, 150, 105));
                button.setBorder(new RoundedBorder(new Color(10, 150, 105), 12, 0));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(COR_SUCESSO);
                button.setBorder(new RoundedBorder(COR_SUCESSO, 12, 0));
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

        // Efeito hover
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

    // --- Layout da tela ---

    private void setupLayout() {
        // Layout em duas colunas
        setLayout(new GridLayout(1, 2, 0, 0));

        // Coluna da esquerda - Painel ilustrativo
        add(createIlustrativePanel());

        // Coluna da direita - Formulário de cadastro
        JPanel painelCadastro = new JPanel(new GridBagLayout());
        painelCadastro.setBackground(COR_CARD);

        JPanel cardCadastro = createCadastroCard();
        painelCadastro.add(cardCadastro);

        add(painelCadastro);
    }

    private JPanel createIlustrativePanel() {
        JPanel painelImagem = new JPanel(new BorderLayout());
        painelImagem.setBackground(COR_FUNDO_LATERAL); // Usando a cor roxo/violeta para diferenciação

        // Painel para título e subtítulo
        JPanel painelTexto = new JPanel();
        painelTexto.setLayout(new BoxLayout(painelTexto, BoxLayout.Y_AXIS));
        painelTexto.setBackground(new Color(0, 0, 0, 0)); // Transparente
        painelTexto.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Logo grande
        JLabel lblLogo = new JLabel("🚀");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        lblLogo.setForeground(COR_TEXTO_LATERAL); // Texto branco para melhor contraste
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título principal com fonte maior
        JLabel lblTitulo = new JLabel("Comece Agora");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 38)); // Fonte maior
        lblTitulo.setForeground(COR_TEXTO_LATERAL);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo com realce
        JLabel lblSubtitulo = new JLabel("Junte-se à comunidade Minhas Finanças");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 20));
        lblSubtitulo.setForeground(COR_TEXTO_DESTACADO); // Cor com melhor contraste
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Benefícios do cadastro com melhor espaçamento e alinhamento
        String[] beneficios = {
            "✓ Acesso gratuito a todas as funcionalidades",
            "✓ Gerencie suas despesas com facilidade",
            "✓ Crie metas financeiras personalizadas",
            "✓ Acompanhe seu progresso financeiro"
        };

        JPanel painelBeneficios = new JPanel();
        painelBeneficios.setLayout(new BoxLayout(painelBeneficios, BoxLayout.Y_AXIS));
        painelBeneficios.setBackground(new Color(0, 0, 0, 0)); // Transparente
        painelBeneficios.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        for (String beneficio : beneficios) {
            JLabel lblBeneficio = new JLabel(beneficio);
            lblBeneficio.setFont(new Font("Poppins", Font.BOLD, 16)); // Fonte em negrito para melhor legibilidade
            lblBeneficio.setForeground(COR_TEXTO_LATERAL);
            lblBeneficio.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Painel para cada item para garantir melhor alinhamento
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            itemPanel.setBackground(new Color(0, 0, 0, 0)); // Transparente
            itemPanel.add(lblBeneficio);

            painelBeneficios.add(itemPanel);
            painelBeneficios.add(Box.createVerticalStrut(15)); // Espaçamento maior entre itens
        }

        painelTexto.add(Box.createVerticalGlue());
        painelTexto.add(lblLogo);
        painelTexto.add(Box.createVerticalStrut(16));
        painelTexto.add(lblTitulo);
        painelTexto.add(Box.createVerticalStrut(8));
        painelTexto.add(lblSubtitulo);
        painelTexto.add(Box.createVerticalStrut(10));
        painelTexto.add(painelBeneficios);
        painelTexto.add(Box.createVerticalGlue());

        painelImagem.add(painelTexto, BorderLayout.CENTER);

        return painelImagem;
    }

    private JPanel createCadastroCard() {
        JPanel cardCadastro = new JPanel();
        cardCadastro.setLayout(new BoxLayout(cardCadastro, BoxLayout.Y_AXIS));
        cardCadastro.setBackground(COR_CARD);
        cardCadastro.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        cardCadastro.setMaximumSize(new Dimension(440, 600));
        cardCadastro.setPreferredSize(new Dimension(440, 600));

        // === SEÇÃO HEADER ===
        cardCadastro.add(createHeader());
        cardCadastro.add(Box.createVerticalStrut(30));

        // === SEÇÃO FORMULÁRIO ===
        cardCadastro.add(createFormSection());
        cardCadastro.add(Box.createVerticalStrut(30));

        // === SEÇÃO AÇÕES ===
        cardCadastro.add(createActionSection());

        return cardCadastro;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(COR_CARD);

        // Título da seção de cadastro
        JLabel lblTitulo = new JLabel("Crie sua conta");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
        lblTitulo.setForeground(COR_TEXTO_PRIMARIO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Preencha os dados para se cadastrar");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 16));
        lblSubtitulo.setForeground(COR_TEXTO_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDicaUsuario = new JLabel("Mínimo 3 caracteres, sem espaços");
        lblDicaUsuario.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblDicaUsuario.setForeground(COR_TEXTO_TERCIARIO);
        lblDicaUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        formSection.add(lblUsuario);
        formSection.add(Box.createVerticalStrut(8));
        formSection.add(txtUsuario);
        formSection.add(Box.createVerticalStrut(4));
        formSection.add(lblDicaUsuario);
        formSection.add(Box.createVerticalStrut(20));

        // Campo Senha
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setFont(new Font("Poppins", Font.PLAIN, 15));
        lblSenha.setForeground(COR_TEXTO_PRIMARIO);
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDicaSenha = new JLabel("Mínimo 6 caracteres para sua segurança");
        lblDicaSenha.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblDicaSenha.setForeground(COR_TEXTO_TERCIARIO);
        lblDicaSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        formSection.add(lblSenha);
        formSection.add(Box.createVerticalStrut(8));
        formSection.add(txtSenha);
        formSection.add(Box.createVerticalStrut(4));
        formSection.add(lblDicaSenha);
        formSection.add(Box.createVerticalStrut(20));

        // Campo Confirmar Senha
        JLabel lblConfirmarSenha = new JLabel("Confirmar senha");
        lblConfirmarSenha.setFont(new Font("Poppins", Font.PLAIN, 15));
        lblConfirmarSenha.setForeground(COR_TEXTO_PRIMARIO);
        lblConfirmarSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtConfirmarSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtConfirmarSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        formSection.add(lblConfirmarSenha);
        formSection.add(Box.createVerticalStrut(8));
        formSection.add(txtConfirmarSenha);
        formSection.add(Box.createVerticalStrut(20));

        return formSection;
    }

    private JPanel createActionSection() {
        JPanel actionSection = new JPanel();
        actionSection.setLayout(new BoxLayout(actionSection, BoxLayout.Y_AXIS));
        actionSection.setBackground(COR_CARD);

        // Botão de cadastro
        btnCadastrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        // Mensagem de status
        lblMensagem.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Separador
        JPanel separadorPanel = new JPanel();
        separadorPanel.setLayout(new BoxLayout(separadorPanel, BoxLayout.X_AXIS));
        separadorPanel.setBackground(COR_CARD);
        separadorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        separadorPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JSeparator separator = new JSeparator();
        separator.setForeground(COR_BORDA);
        separator.setBackground(COR_BORDA);

        separadorPanel.add(separator);

        // Botão de cancelar/voltar
        btnCancelar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCancelar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        // Texto de política de privacidade
        JLabel lblPolitica = new JLabel("Ao se cadastrar, você concorda com nossos termos de uso");
        lblPolitica.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblPolitica.setForeground(COR_TEXTO_TERCIARIO);
        lblPolitica.setAlignmentX(Component.LEFT_ALIGNMENT);

        actionSection.add(btnCadastrar);
        actionSection.add(Box.createVerticalStrut(12));
        actionSection.add(lblMensagem);
        actionSection.add(separadorPanel);
        actionSection.add(btnCancelar);
        actionSection.add(Box.createVerticalStrut(20));
        actionSection.add(lblPolitica);

        return actionSection;
    }

    // --- Métodos de eventos e lógica ---

    private void setupEventos() {
        // Eventos dos botões
        btnCadastrar.addActionListener(e -> realizarCadastro());
        btnCancelar.addActionListener(e -> dispose());

        // Navegação por teclado
        KeyAdapter enterPressionado = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.getSource() == txtUsuario) {
                        txtSenha.requestFocus();
                    } else if (e.getSource() == txtSenha) {
                        txtConfirmarSenha.requestFocus();
                    } else if (e.getSource() == txtConfirmarSenha) {
                        realizarCadastro();
                    }
                }
            }
        };

        txtUsuario.addKeyListener(enterPressionado);
        txtSenha.addKeyListener(enterPressionado);
        txtConfirmarSenha.addKeyListener(enterPressionado);

        // ESC para cancelar
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    // --- Classes e métodos auxiliares para o design ---

    private JTextField createTextFieldWithIcon(String icon, String placeholder) {
        JTextField textField = new JTextField(placeholder);
        return (JTextField) setupFieldWithIcon(textField, icon, placeholder);
    }

    private JPasswordField createPasswordFieldWithIcon(String icon, String placeholder) {
        JPasswordField passwordField = new JPasswordField(placeholder);
        passwordField.setEchoChar((char) 0); // Mostra o placeholder
        return (JPasswordField) setupFieldWithIcon(passwordField, icon, placeholder);
    }

    private JComponent setupFieldWithIcon(JComponent field, String icon, String placeholder) {
        field.setFont(new Font("Poppins", Font.PLAIN, 15));
        field.setForeground(COR_TEXTO_PLACEHOLDER);
        field.setBackground(Color.WHITE);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new RoundedBorder(COR_BORDA, 12, 1));
        panel.add(lblIcon, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        field.setBorder(BorderFactory.createEmptyBorder(16, 5, 16, 15));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                panel.setBorder(new RoundedBorder(COR_BORDA_FOCO, 12, 2));
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
                panel.setBorder(new RoundedBorder(COR_BORDA, 12, 1));
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

    // --- Métodos para funcionalidade que devem ser mantidos ---

    private void garantirFocoInicial() {
        // Aguardar um pequeno delay para garantir que todos os componentes estejam prontos
        Timer focusTimer = new Timer(150, e -> {
            SwingUtilities.invokeLater(() -> {
                txtUsuario.requestFocusInWindow();
            });
        });
        focusTimer.setRepeats(false);
        focusTimer.start();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            SwingUtilities.invokeLater(() -> {
                // Pequeno delay para garantir que a janela esteja completamente renderizada
                Timer visibilityTimer = new Timer(100, e -> {
                    toFront();
                    requestFocus();
                    txtUsuario.requestFocusInWindow();
                });
                visibilityTimer.setRepeats(false);
                visibilityTimer.start();
            });
        }
    }

    private void realizarCadastro() {
        String nomeUsuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String confirmarSenha = new String(txtConfirmarSenha.getPassword());

        // Remover texto placeholder
        if (nomeUsuario.equals("Nome de usuário")) {
            nomeUsuario = "";
        }
        if (senha.equals("Senha")) {
            senha = "";
        }
        if (confirmarSenha.equals("Confirmar senha")) {
            confirmarSenha = "";
        }

        if (!validarCampos(nomeUsuario, senha, confirmarSenha)) {
            return;
        }

        try {
            btnCadastrar.setEnabled(false);
            btnCadastrar.setText("Cadastrando...");
            exibirMensagem("Criando usuário...", COR_PRIMARIA);

            Usuario usuario = new Usuario();
            usuario.setNome(nomeUsuario);
            usuario.setSenha(senha);

            usuarioController.cadastrarUsuario(usuario);

            exibirMensagem("Usuário cadastrado com sucesso!", COR_SUCESSO);

            atualizarDashboardParaNovoUsuario();

            Timer timer = new Timer(1500, e -> {
                SwingUtilities.invokeLater(() -> {
                    if (telaLogin != null) {
                        telaLogin.prepararParaRetornoFoco();
                    }

                    Timer closeTimer = new Timer(100, evt -> {
                        setVisible(false);
                        dispose();

                        if (telaLogin != null) {
                            telaLogin.exibirMensagemCadastroSucesso();
                        }
                    });
                    closeTimer.setRepeats(false);
                    closeTimer.start();
                });
            });
            timer.setRepeats(false);
            timer.start();

        } catch (BusinessException e) {
            exibirMensagem(e.getMessage(), COR_ERRO);
            SwingUtilities.invokeLater(() -> {
                if (e.getMessage().toLowerCase().contains("usuário")) {
                    txtUsuario.requestFocusInWindow();
                    txtUsuario.selectAll();
                } else {
                    txtSenha.requestFocusInWindow();
                    txtSenha.selectAll();
                }
            });
        } catch (Exception e) {
            exibirMensagem("Erro interno do sistema. Tente novamente.", COR_ERRO);
            System.err.println("Erro no cadastro: " + e.getMessage());
            SwingUtilities.invokeLater(() -> {
                txtUsuario.requestFocusInWindow();
            });
        } finally {
            btnCadastrar.setEnabled(true);
            btnCadastrar.setText("Criar conta");
        }
    }

    private void atualizarDashboardParaNovoUsuario() {
        try {
            System.out.println("Novo usuário criado - dashboard será personalizado no próximo login");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar dashboard após cadastro de usuário: " + e.getMessage());
        }
    }

    private boolean validarCampos(String nomeUsuario, String senha, String confirmarSenha) {
        if (nomeUsuario.isEmpty()) {
            exibirMensagem("Por favor, informe o nome do usuário.", COR_ERRO);
            SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());
            return false;
        }

        if (nomeUsuario.length() < 3) {
            exibirMensagem("O nome do usuário deve ter pelo menos 3 caracteres.", COR_ERRO);
            SwingUtilities.invokeLater(() -> {
                txtUsuario.requestFocusInWindow();
                txtUsuario.selectAll();
            });
            return false;
        }

        if (nomeUsuario.contains(" ")) {
            exibirMensagem("O nome do usuário não pode conter espaços.", COR_ERRO);
            SwingUtilities.invokeLater(() -> {
                txtUsuario.requestFocusInWindow();
                txtUsuario.selectAll();
            });
            return false;
        }

        if (senha.isEmpty()) {
            exibirMensagem("Por favor, informe a senha.", COR_ERRO);
            SwingUtilities.invokeLater(() -> txtSenha.requestFocusInWindow());
            return false;
        }

        if (senha.length() < 6) {
            exibirMensagem("A senha deve ter pelo menos 6 caracteres.", COR_ERRO);
            SwingUtilities.invokeLater(() -> {
                txtSenha.requestFocusInWindow();
                txtSenha.selectAll();
            });
            return false;
        }

        if (confirmarSenha.isEmpty()) {
            exibirMensagem("Por favor, confirme a senha.", COR_ERRO);
            SwingUtilities.invokeLater(() -> txtConfirmarSenha.requestFocusInWindow());
            return false;
        }

        if (!senha.equals(confirmarSenha)) {
            exibirMensagem("A confirmação de senha não confere.", COR_ERRO);
            SwingUtilities.invokeLater(() -> {
                txtConfirmarSenha.requestFocusInWindow();
                txtConfirmarSenha.selectAll();
            });
            return false;
        }

        return true;
    }

    private void exibirMensagem(String mensagem, Color cor) {
        lblMensagem.setText(mensagem);
        lblMensagem.setForeground(cor);
    }
}
