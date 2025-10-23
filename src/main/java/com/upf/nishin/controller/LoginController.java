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
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {

    private String email;
    private String senha;
    private UsuarioEntity usuarioLogado;

    @Inject
    private UsuarioFacade usuarioFacade;

    public String login() {
        try {
            List<UsuarioEntity> usuarios = usuarioFacade.getEntityManager()
                    .createQuery("SELECT u FROM UsuarioEntity u WHERE u.email = :email AND u.senha = :senha", UsuarioEntity.class)
                    .setParameter("email", email.trim())
                    .setParameter("senha", senha.trim())
                    .getResultList();

            if (usuarios.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "E-mail ou senha incorretos."));
                return null;
            }

            usuarioLogado = usuarios.get(0);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", usuarioLogado);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Login realizado com sucesso!"));

            return "/index.xhtml?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro no login", e.getMessage()));
            return null;
        }
    }

    public void logout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
    }

    public boolean isLogado() {
        return usuarioLogado != null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UsuarioEntity getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(UsuarioEntity usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }
}
