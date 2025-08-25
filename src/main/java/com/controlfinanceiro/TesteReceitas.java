package com.controlfinanceiro;

import com.controlfinanceiro.controller.TransacaoController;
import com.controlfinanceiro.model.Transacao;
import com.controlfinanceiro.model.enums.TipoTransacao;
import com.controlfinanceiro.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TesteReceitas {
    public static void main(String[] args) {
        System.out.println("=== TESTE DE RECEITAS ===");

        TransacaoController controller = new TransacaoController();

        try {
            // Testar se há transações no banco
            LocalDate hoje = LocalDate.now();
            LocalDate inicioMes = hoje.withDayOfMonth(1);
            LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

            System.out.println("Buscando transações de " + inicioMes + " até " + fimMes);

            List<Transacao> todasTransacoes = controller.listarTransacoesPorPeriodo(inicioMes, fimMes);
            System.out.println("Total de transações encontradas: " + todasTransacoes.size());

            // Listar todas as transações
            if (todasTransacoes.isEmpty()) {
                System.out.println("NENHUMA TRANSAÇÃO ENCONTRADA!");

                // Tentar buscar todas as receitas
                List<Transacao> todasReceitas = controller.listarTransacoesPorTipo(TipoTransacao.RECEITA);
                System.out.println("Total de receitas em todo o banco: " + todasReceitas.size());

                for (Transacao receita : todasReceitas) {
                    System.out.println("Receita: " + receita.getDescricao() +
                                     " - Valor: " + receita.getValor() +
                                     " - Data: " + receita.getDataTransacao());
                }
            } else {
                System.out.println("Transações encontradas:");
                BigDecimal totalReceitas = BigDecimal.ZERO;
                BigDecimal totalDespesas = BigDecimal.ZERO;

                for (Transacao t : todasTransacoes) {
                    System.out.println("- " + t.getTipo() + ": " + t.getDescricao() +
                                     " - R$ " + t.getValor() +
                                     " - Data: " + t.getDataTransacao());

                    if (t.getTipo() == TipoTransacao.RECEITA) {
                        totalReceitas = totalReceitas.add(t.getValor());
                    } else if (t.getTipo() == TipoTransacao.DESPESA) {
                        totalDespesas = totalDespesas.add(t.getValor());
                    }
                }

                System.out.println("\nTOTAIS CALCULADOS:");
                System.out.println("Total Receitas: R$ " + totalReceitas);
                System.out.println("Total Despesas: R$ " + totalDespesas);
                System.out.println("Saldo: R$ " + totalReceitas.subtract(totalDespesas));
            }

        } catch (BusinessException e) {
            System.err.println("Erro ao testar receitas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
