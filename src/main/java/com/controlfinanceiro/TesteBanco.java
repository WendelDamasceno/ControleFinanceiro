package com.controlfinanceiro;

import com.controlfinanceiro.util.ConnectionFactory;
import com.controlfinanceiro.controller.TransacaoController;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class TesteBanco {
    public static void main(String[] args) {
        try {
            // Testar conexão
            System.out.println("Testando conexão com o banco...");
            Connection conn = ConnectionFactory.getConnection();
            System.out.println("Conexão OK: " + !conn.isClosed());
            conn.close();

            // Testar controller
            System.out.println("Testando TransacaoController...");
            TransacaoController controller = new TransacaoController();

            LocalDate hoje = LocalDate.now();
            LocalDate inicioMes = hoje.withDayOfMonth(1);
            LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

            System.out.println("Buscando transações de " + inicioMes + " até " + fimMes);
            List<Transacao> transacoes = controller.listarTransacoesPorPeriodo(inicioMes, fimMes);
            System.out.println("Encontradas " + transacoes.size() + " transações");

            for (Transacao t : transacoes) {
                System.out.println("- " + t.getDescricao() + " | " + t.getTipo() + " | R$ " + t.getValor());
            }

        } catch (Exception e) {
            System.err.println("Erro no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
