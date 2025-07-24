package com.controlfinanceiro.model.enums;

public enum TipoTransacao {
    RECEITA ("Receita", "Entrada de dinheiro"),
    DESPESA("Despesa", "Saída de Dinheiro");

    private String descricao;
    private String detalhamento;
    TipoTransacao(String descricao, String detalhamento) {
        this.descricao = descricao;
        this.detalhamento = detalhamento;
    }
    public String getDescricao() {
        return descricao;
    }
    public String getDetalhamento() {
        return detalhamento;
    }


}
