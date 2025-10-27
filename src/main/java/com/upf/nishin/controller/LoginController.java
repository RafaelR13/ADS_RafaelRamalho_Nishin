/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.controller;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.UsuarioEntity;
import com.upf.nishin.facade.UsuarioFacade;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {

    @EJB
    private UsuarioFacade usuarioFacade;

    private UsuarioEntity usuario;

    public LoginController() {
    }

    @PostConstruct
    public void init() {
        usuario = new UsuarioEntity();
    }

    /**
     * Valida login e senha e mantém o usuário na sessão.
     */
    public String validarLogin() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);

        try {
            UsuarioEntity usuarioDB = usuarioFacade.buscarPorEmail(usuario.getEmail(), usuario.getSenha());

            if (usuarioDB != null && usuarioDB.getIdUsuario() != null) {
                session.setAttribute("usuarioLogado", usuarioDB);
                this.usuario = usuarioDB;

                FacesMessage fm = new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Sucesso!",
                        "Login realizado com sucesso!");
                FacesContext.getCurrentInstance().addMessage(null, fm);

                return "/index.xhtml?faces-redirect=true";
            } else {
                FacesMessage fm = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Falha no Login!",
                        "E-mail ou senha incorretos!");
                FacesContext.getCurrentInstance().addMessage(null, fm);
                return null;
            }

        } catch (Exception e) {
            FacesMessage fm = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Erro no Login!",
                    e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fm);
            return null;
        }
    }

    /**
     * Retorna o usuário logado da sessão.
     */
    public UsuarioEntity getUsuarioLogado() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        return (UsuarioEntity) session.getAttribute("usuarioLogado");
    }

    /**
     * Verifica se há usuário logado.
     */
    public boolean isLogado() {
        return getUsuarioLogado() != null;
    }

    /**
     * Faz logout e invalida a sessão.
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "/login.xhtml?faces-redirect=true";
    }

    // Getters e Setters
    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }
}