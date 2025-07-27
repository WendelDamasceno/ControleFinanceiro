package com.controlfinanceiro.model.enums;
/*
* TIpos de transação financeira*/
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

    /*
    * Converter uma String de volta paara um membro a enumeração*/
    public static TipoTransacao fromString(String tipo){
        if(tipo == null) return null;
        for (TipoTransacao t : values()) {
            if(t.name().equalsIgnoreCase(tipo)){
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de transação inválido: " + tipo);
    }


    @Override
    public String toString() {
        return descricao;
    }
}
