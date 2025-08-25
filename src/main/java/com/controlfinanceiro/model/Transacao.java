package com.controlfinanceiro.model;

import com.controlfinanceiro.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transacao {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataTransacao; // Data da transação no formato "dd/MM/yyyy"
    private TipoTransacao tipo; // Pode ser RECEITA ou DESPESA
    private Long categoriaId; // Referência à categoria da transação
    private Categoria categoria;
    private Usuario usuario; // Usuário associado à transação
    private Long usuarioId; // ID do usuário associado à transação
    private String observacao; // Observações adicionais sobre a transação
    private boolean ativo; // Indica se a transação está ativa ou não
    private LocalDateTime dataCriacao; // Data de criação no formato "dd/MM/yyyy HH:mm:ss"
    private LocalDateTime dataAtualizacao; // Data de atualização no formato "dd/MM/yyyy HH:mm:ss"

    // Construtores

    public Transacao() {
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Transacao (String descricao, BigDecimal valor, TipoTransacao tipo) {
        this();
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
    }

    public Transacao(String descricao, BigDecimal valor, LocalDate dataTransacao, TipoTransacao tipo, Long categoriaId) {
        this(descricao, valor, tipo);
        this.dataTransacao = dataTransacao;
        this.categoriaId = categoriaId;
    }

    //Getters e Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
        dataAtualizacao = LocalDateTime.now();
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        if(categoria != null){
            this.categoriaId = categoria.getId();
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            this.usuarioId = usuario.getId();
        }
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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

    //Metodos

    public void inativar() {
        setAtivo(false);
    }
    public void ativar() {
        setAtivo(true);
    }
    public boolean isReceita() {
        return TipoTransacao.RECEITA.equals(tipo);
    }
    public boolean isDespesa() {
        return TipoTransacao.DESPESA.equals(tipo);
    }
    public String getNomeCategoria() {
        return categoria != null ? categoria.getNome() : "Sem Categoria";
    }

    //Validation

    public boolean isValid() {
        return descricao != null && !descricao.trim().isEmpty() &&
                valor != null && valor.compareTo(BigDecimal.ZERO) > 0 &&
                dataTransacao != null &&
                tipo != null;
    }

    //Equals and HashCode


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(id, transacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //toString


    @Override
    public String toString() {
        return String.format("%s: %s - R$ %s", tipo != null ? tipo.getDescricao() : "N/A", descricao != null ? descricao : "Sem descrição", valor != null ? valor : "0.00");
    }
}
