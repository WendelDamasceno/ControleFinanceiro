package com.controlfinanceiro.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Objects;

public class Orcamento {
    private Long id;
    private Long categoriaId; // Referência à categoria do orçamento
    private Categoria categoria;
    private BigDecimal valorLimite; // Valor limite do orçamento
    private int mes; // Mês do orçamento (1-12)
    private int ano; // Ano do orçamento
    private String descricao;
    private boolean ativo; // Indica se o orçamento está ativo ou não
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // valor gasto real (relatórios)
    private BigDecimal valorGasto; // Valor já gasto do orçamento
    private BigDecimal valorDisponivel; // Valor disponível do orçamento

    // Construtores

    public Orcamento() {
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();

        YearMonth agora = YearMonth.now();
        this.mes = agora.getMonthValue();
        this.ano = agora.getYear();
    }

    public Orcamento (Long categoriaId, BigDecimal valorLimite) {
        this();
        this.categoriaId = categoriaId;
        this.valorLimite = valorLimite;
    }

    public Orcamento (Long categoriaId, BigDecimal valorLimite, int mes, int ano){
        this(categoriaId, valorLimite);
        this.mes = mes;
        this.ano = ano;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (categoria != null) {
            this.categoriaId = categoria.getId();
        }
    }

    public BigDecimal getValorLimite() {
        return valorLimite;
    }

    public void setValorLimite(BigDecimal valorLimite) {
        this.valorLimite = valorLimite;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        if(mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve ser um valor entre 1 e 12.");
        }
        this.mes = mes;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        if(ano < 2020){
            throw new IllegalArgumentException("Ano deve ser maior que 2020.");
        }
        this.ano = ano;
        this.dataAtualizacao = LocalDateTime.now();
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

    public BigDecimal getValorGasto() {
        return valorGasto != null ? valorGasto : BigDecimal.ZERO;
    }

    public void setValorGasto(BigDecimal valorGasto) {
        this.valorGasto = valorGasto;
    }

    public BigDecimal getValorDisponivel() {
        if(valorLimite == null) return BigDecimal.ZERO;
        BigDecimal gasto = getValorGasto();
        return valorLimite.subtract(gasto);
    }


    //Metodos de conveniência

    public void inativar() {
        setAtivo(false);
    }
    public void ativar() {
        setAtivo(true);
    }
    public String getPeriodo() {
        return String.format("%02d/%d", mes, ano);
    }
    public YearMonth getYearMonth() {
        return YearMonth.of(ano, mes);
    }
    public void setPeriodo(YearMonth periodo){
        this.ano = periodo.getYear();
        this.mes = periodo.getMonthValue();
        this.dataAtualizacao = LocalDateTime.now();
    }
    public String getNomeCategoria() {
        return categoria != null ? categoria.getNome() : "Sem categoria";
    }

    //Métodos de status do orçamento

    public boolean isEstourado() {
        return getValorDisponivel().compareTo(valorLimite) > 0;
    }
    public boolean isNoLimite(){
        return getValorGasto().compareTo(valorLimite) == 0;
    }
    public boolean isDentroDoOrcamento() {
        return getValorGasto().compareTo(valorLimite) < 0;
    }
    public double getPercentualUtilizado(){
        if (valorLimite == null || valorLimite.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0; // Evita divisão por zero
        }
        return getValorGasto().doubleValue() / valorLimite.doubleValue() * 100.0;
    }
    public String getStatusOrcamento() {
        if (isEstourado()) {
            return "Acima do orçamento";
        } else if (isNoLimite()) {
            return "No Limite";
        } else if (isDentroDoOrcamento()) {
            return "Dentro do Orçamento";
        }
        return "Desconhecido";
    }

    // Validação

    public boolean isValid(){
        return categoriaId != null && valorLimite != null && valorLimite.compareTo(BigDecimal.ZERO) > 0 && mes >= 1 && mes <= 12 && ano >= 2020;
    }

    //Equals e HashCode


    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Orcamento orcamento = (Orcamento) o;
        return Objects.equals(id, orcamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString

    @Override
    public  String toString() {
        return String.format("Orçamento %s: R$ %s (%s)", getPeriodo(), valorLimite != null ? valorLimite : "0.00", getNomeCategoria());
    }
}
