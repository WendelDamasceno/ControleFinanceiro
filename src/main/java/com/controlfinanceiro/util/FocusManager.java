package com.controlfinanceiro.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Utilitário para gerenciamento centralizado de foco em aplicações Swing
 * Resolve problemas de threading e timing no gerenciamento de foco entre telas
 */
public class FocusManager {

    // Delays padrão para diferentes situações
    private static final int DELAY_FOCO_INICIAL = 150;
    private static final int DELAY_RETORNO_MODAL = 200;
    private static final int DELAY_VISIBILIDADE = 100;
    private static final int DELAY_FECHAMENTO = 50;

    /**
     * Garante que um componente receba foco inicial de forma segura
     * @param component Componente que deve receber o foco
     * @param selectAll Se deve selecionar todo o texto (para campos de texto)
     */
    public static void garantirFocoInicial(JComponent component, boolean selectAll) {
        SwingUtilities.invokeLater(() -> {
            Timer timer = new Timer(DELAY_FOCO_INICIAL, e -> {
                SwingUtilities.invokeLater(() -> {
                    component.requestFocusInWindow();
                    if (selectAll && component instanceof JTextField) {
                        ((JTextField) component).selectAll();
                    }
                });
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    /**
     * Garante que um componente receba foco inicial de forma segura (sem seleção)
     * @param component Componente que deve receber o foco
     */
    public static void garantirFocoInicial(JComponent component) {
        garantirFocoInicial(component, false);
    }

    /**
     * Prepara uma janela para receber foco ao retornar de uma tela modal
     * @param window Janela que deve receber o foco
     */
    public static void prepararRetornoModal(Window window) {
        SwingUtilities.invokeLater(() -> {
            if (window instanceof Frame) {
                ((Frame) window).setState(Frame.NORMAL);
            }
            window.toFront();
            window.requestFocus();
        });
    }

    /**
     * Configura callback para retorno de foco quando uma tela modal é fechada
     * @param telaModal Tela modal que será fechada
     * @param telaOrigem Tela que deve receber o foco de volta
     * @param componenteFoco Componente específico que deve receber o foco
     * @param selectAll Se deve selecionar todo o texto do componente
     */
    public static void configurarRetornoFocoModal(Dialog telaModal, Window telaOrigem,
                                                  JComponent componenteFoco, boolean selectAll) {
        telaModal.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    prepararRetornoModal(telaOrigem);

                    Timer focusTimer = new Timer(DELAY_RETORNO_MODAL, evt -> {
                        SwingUtilities.invokeLater(() -> {
                            componenteFoco.requestFocusInWindow();
                            if (selectAll && componenteFoco instanceof JTextField) {
                                ((JTextField) componenteFoco).selectAll();
                            }
                        });
                    });
                    focusTimer.setRepeats(false);
                    focusTimer.start();
                });
            }
        });
    }

    /**
     * Configura foco ao tornar uma janela visível
     * @param window Janela que está sendo tornada visível
     * @param componenteFoco Componente que deve receber o foco
     * @param selectAll Se deve selecionar todo o texto do componente
     */
    public static void configurarFocoVisibilidade(Window window, JComponent componenteFoco, boolean selectAll) {
        SwingUtilities.invokeLater(() -> {
            Timer timer = new Timer(DELAY_VISIBILIDADE, e -> {
                window.toFront();
                window.requestFocus();
                if (componenteFoco != null) {
                    componenteFoco.requestFocusInWindow();
                    if (selectAll && componenteFoco instanceof JTextField) {
                        ((JTextField) componenteFoco).selectAll();
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    /**
     * Executa uma ação de fechamento com timing apropriado para transferência de foco
     * @param acaoFechamento Ação a ser executada para fechar a tela
     * @param acaoAposFechamento Ação a ser executada após o fechamento
     */
    public static void executarFechamentoComFoco(Runnable acaoFechamento, Runnable acaoAposFechamento) {
        SwingUtilities.invokeLater(() -> {
            acaoFechamento.run();

            Timer timer = new Timer(DELAY_FECHAMENTO, e -> {
                SwingUtilities.invokeLater(acaoAposFechamento);
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    /**
     * Configura foco em caso de erro/validação
     * @param component Componente que deve receber o foco
     * @param selectAll Se deve selecionar todo o texto
     */
    public static void focarAposErro(JComponent component, boolean selectAll) {
        SwingUtilities.invokeLater(() -> {
            component.requestFocusInWindow();
            if (selectAll && component instanceof JTextField) {
                ((JTextField) component).selectAll();
            }
        });
    }

    /**
     * Alterna foco entre dois componentes baseado em uma condição
     * @param condicao Condição para determinar qual componente deve receber foco
     * @param componenteSeVerdadeiro Componente que recebe foco se condição for verdadeira
     * @param componenteSeFalso Componente que recebe foco se condição for falsa
     * @param selectAll Se deve selecionar todo o texto
     */
    public static void focarCondicional(boolean condicao, JComponent componenteSeVerdadeiro,
                                      JComponent componenteSeFalso, boolean selectAll) {
        JComponent alvo = condicao ? componenteSeVerdadeiro : componenteSeFalso;
        garantirFocoInicial(alvo, selectAll);
    }
}
