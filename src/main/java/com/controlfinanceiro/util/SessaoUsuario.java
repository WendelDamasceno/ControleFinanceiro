package com.controlfinanceiro.util;

import com.controlfinanceiro.model.Usuario;

/**
 * Classe singleton para gerenciar a sessão do usuário logado
 */
public class SessaoUsuario {
    private static SessaoUsuario instance;
    private Usuario usuarioLogado;

    private SessaoUsuario() {
        // Construtor privado para implementar Singleton
    }

    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    /**
     * Define o usuário logado na sessão
     * @param usuario O usuário que fez login
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    /**
     * Retorna o usuário atualmente logado
     * @return O usuário logado ou null se ninguém estiver logado
     */
    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    /**
     * Verifica se há um usuário logado
     * @return true se há usuário logado, false caso contrário
     */
    public boolean isUsuarioLogado() {
        return this.usuarioLogado != null;
    }

    /**
     * Retorna o ID do usuário logado
     * @return ID do usuário ou null se ninguém estiver logado
     */
    public Long getIdUsuarioLogado() {
        return isUsuarioLogado() ? usuarioLogado.getId() : null;
    }

    /**
     * Retorna o nome do usuário logado
     * @return Nome do usuário ou "Usuário" se ninguém estiver logado
     */
    public String getNomeUsuarioLogado() {
        return isUsuarioLogado() ? usuarioLogado.getNome() : "Usuário";
    }

    /**
     * Encerra a sessão do usuário
     */
    public void logout() {
        this.usuarioLogado = null;
    }
}
