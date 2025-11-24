/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.controller;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.*;
import com.upf.nishin.facade.*;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("pedidoController")
@ViewScoped
public class PedidoController implements Serializable {

    @Inject
    private PedidoFacade pedidoFacade;

    @Inject
    private UsuarioController usuarioController;

    private List<PedidoEntity> pedidosDoUsuario;

    @PostConstruct
    public void init() {
        carregarPedidosDoUsuario();
    }

    public void carregarPedidosDoUsuario() {

        UsuarioEntity u = usuarioController.getUsuarioLogado();

        if (u != null) {
            pedidosDoUsuario = pedidoFacade.listarPedidosPorUsuario(u);
        } else {
            pedidosDoUsuario = new ArrayList<>();
        }
    }

    public List<PedidoEntity> getListaPedidosDoUsuario() {
        return pedidosDoUsuario;
    }
}

