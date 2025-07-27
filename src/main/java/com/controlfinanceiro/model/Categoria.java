package com.controlfinanceiro.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Categoria {
    private Long id;
    private String nome;
    private String descricao;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtores

    public Categoria() {
        this.ativo = true; // Por padrão, a categoria está ativa
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Categoria(String nome) {
        this();
        this.nome = nome;
    }

    public Categoria(String nome, String descricao) {
        this(nome);
        this.descricao = descricao;
    }

    public Categoria(Long id, String nome, String descricao) {
        this(nome, descricao);
        this.id = id;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
        this.dataCriacao = LocalDateTime.now();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // Métodos

    public void inativar() {
        setAtivo(false);
    }
    public void ativar() {
        setAtivo(true);
    }

    //validate

    public boolean isValid() {
        return nome != null && !nome.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome != null ? nome : "Categoria sem nome";
    }
}
