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
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Named("usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    private UsuarioEntity usuario = new UsuarioEntity();
    private UsuarioEntity selected;

    @Inject
    private UsuarioFacade usuarioFacade;

    // === GETTERS E SETTERS ===
    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public UsuarioEntity getSelected() {
        return selected;
    }

    public void setSelected(UsuarioEntity selected) {
        this.selected = selected;
    }

    public List<UsuarioEntity> getUsuarioList() {
        return usuarioFacade.findAll();
    }

    // === CRUD BÁSICO ===
    public void adicionarUsuario() {
        usuarioFacade.create(usuario);
        usuario = new UsuarioEntity();
    }

    public void editarUsuario() {
        if (selected != null) {
            usuarioFacade.edit(selected);
            selected = null;
        }
    }

    public void deletarUsuario() {
        if (selected != null) {
            usuarioFacade.remove(selected);
            selected = null;
        }
    }

    // === CADASTRO COM LOGIN AUTOMÁTICO ===
    public String cadastrarUsuario() {
        try {
            // 🔹 Garante que a data de cadastro será registrada
            usuario.setDataCadastro(LocalDateTime.now());

            // 🔹 Salva o usuário no banco
            usuarioFacade.create(usuario);

            // 🔹 Atualiza o objeto da sessão com o usuário persistido
            UsuarioEntity usuarioSalvo = usuarioFacade.buscarPorEmail(usuario.getEmail(), usuario.getSenha());

            if (usuarioSalvo != null) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                session.setAttribute("usuarioLogado", usuarioSalvo);

                FacesMessage fm = new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Sucesso!",
                        "Cadastro realizado com sucesso!"
                );
                context.addMessage(null, fm);

                // 🔹 Limpa o formulário
                usuario = new UsuarioEntity();

                // 🔹 Redireciona para a página inicial já logado
                return "/index.xhtml?faces-redirect=true";
            } else {
                FacesMessage fm = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Erro!",
                        "Usuário não pôde ser recuperado após cadastro."
                );
                FacesContext.getCurrentInstance().addMessage(null, fm);
                return null;
            }

        } catch (Exception e) {
            FacesMessage fm = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Erro!",
                    "Falha ao cadastrar: " + e.getMessage()
            );
            FacesContext.getCurrentInstance().addMessage(null, fm);
            return null;
        }
    }

    // === LOGOUT ===
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "/login.xhtml?faces-redirect=true";
    }

    // === VERIFICAÇÃO DE LOGIN ===
    public boolean isLogado() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        return session != null && session.getAttribute("usuarioLogado") != null;
    }

    public UsuarioEntity getUsuarioLogado() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        return session != null ? (UsuarioEntity) session.getAttribute("usuarioLogado") : null;
    }
}

