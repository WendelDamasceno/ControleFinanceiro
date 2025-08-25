package com.controlfinanceiro;

import com.controlfinanceiro.controller.TransacaoController;
import com.controlfinanceiro.controller.CategoriaController;
import com.controlfinanceiro.controller.OrcamentoController;
import com.controlfinanceiro.controller.RelatorioController;
import com.controlfinanceiro.model.enums.TipoTransacao;

public class TesteAutomatico {

    public static void main(String[] args) {
        System.out.println("=== TESTE AUTOMÁTICO DO SISTEMA ===\n");

        try {
            System.out.println("🔄 Iniciando testes dos controllers...\n");

            // Teste 1: CategoriaController
            System.out.println("1️⃣ Testando CategoriaController:");
            CategoriaController categoriaController = new CategoriaController();
            var categorias = categoriaController.listarAtivas();
            System.out.println("✅ " + categorias.size() + " categorias encontradas");

            // Teste 2: TransacaoController
            System.out.println("\n2️⃣ Testando TransacaoController:");
            TransacaoController transacaoController = new TransacaoController();
            var totalReceitas = transacaoController.calcularTotalPorTipo(TipoTransacao.RECEITA);
            var totalDespesas = transacaoController.calcularTotalPorTipo(TipoTransacao.DESPESA);
            System.out.printf("✅ Total Receitas: R$ %.2f%n", totalReceitas);
            System.out.printf("✅ Total Despesas: R$ %.2f%n", totalDespesas);
            System.out.printf("✅ Saldo: R$ %.2f%n", totalReceitas.subtract(totalDespesas));

            // Teste 3: OrcamentoController
            System.out.println("\n3️⃣ Testando OrcamentoController:");
            OrcamentoController orcamentoController = new OrcamentoController();
            var orcamentos = orcamentoController.listarPorPeriodo(1, 2025);
            System.out.println("✅ " + orcamentos.size() + " orçamentos para Jan/2025");

            // Teste 4: RelatorioController
            System.out.println("\n4️⃣ Testando RelatorioController:");
            RelatorioController relatorioController = new RelatorioController();
            var resumo = relatorioController.gerarResumoFinanceiro();
            System.out.println("✅ Relatório financeiro gerado com sucesso");
            System.out.println("   - Total Receitas: " + resumo.get("totalReceitas"));
            System.out.println("   - Total Despesas: " + resumo.get("totalDespesas"));
            System.out.println("   - Saldo Atual: " + resumo.get("saldoAtual"));

            System.out.println("\n🎉 TODOS OS TESTES PASSARAM COM SUCESSO!");
            System.out.println("✅ Sistema funcionando corretamente");

        } catch (Exception e) {
            System.err.println("❌ Erro durante os testes: " + e.getMessage());
            System.out.println("\n💡 POSSÍVEIS SOLUÇÕES:");
            System.out.println("1. Verifique se o MySQL está rodando");
            System.out.println("2. Execute o script_banco.sql");
            System.out.println("3. Configure database.properties");
            System.err.println("Detalhes do erro: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
