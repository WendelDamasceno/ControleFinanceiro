package com.controlfinanceiro;

import com.controlfinanceiro.controller.CategoriaController;
import com.controlfinanceiro.model.Categoria;
import com.controlfinanceiro.exception.BusinessException;

import java.util.List;

public class TesteCategoria {
    public static void main(String[] args) {
        try {
            CategoriaController controller = new CategoriaController();

            System.out.println("Listando todas as categorias...");
            List<Categoria> categorias = controller.listarCategorias();

            for (Categoria cat : categorias) {
                System.out.println("ID: " + cat.getId() + " | Nome: " + cat.getNome() + " | Descrição: " + cat.getDescricao());
            }

            // Procurar e remover categorias relacionadas a salário
            for (Categoria cat : categorias) {
                String nome = cat.getNome().toLowerCase();
                if (nome.contains("salário") || nome.contains("salario")) {
                    System.out.println("\nRemoção da categoria de salário: " + cat.getNome());
                    controller.excluirCategoria(cat.getId());
                    System.out.println("Categoria removida com sucesso!");
                }
            }

            System.out.println("\nListando categorias após remoção...");
            categorias = controller.listarCategorias();
            for (Categoria cat : categorias) {
                System.out.println("ID: " + cat.getId() + " | Nome: " + cat.getNome());
            }

        } catch (BusinessException e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
