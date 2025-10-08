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

    // === CRUD ===
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
}
