package com.controlfinanceiro;

import com.controlfinanceiro.view.TelaLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Iniciando aplicação MeuBolso");

        SwingUtilities.invokeLater(() -> {
            try {
                new TelaLogin().setVisible(true);
                logger.info("Tela de login iniciada");
            } catch (Exception e) {
                logger.error("Erro ao iniciar aplicação", e);
                JOptionPane.showMessageDialog(null,
                    "Erro ao iniciar a aplicação: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
