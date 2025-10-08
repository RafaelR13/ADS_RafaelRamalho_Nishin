/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.controller;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.ProdutoEntity;
import com.upf.nishin.facade.ProdutoFacade;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("produtoController")
@SessionScoped
public class ProdutoController implements Serializable {

    private ProdutoEntity produto = new ProdutoEntity();
    private ProdutoEntity selected;

    @Inject
    private ProdutoFacade produtoFacade;

    public ProdutoEntity getProduto() {
        return produto;
    }
    public void setProduto(ProdutoEntity produto) {
        this.produto = produto;
    }

    public ProdutoEntity getSelected() {
        return selected;
    }
    public void setSelected(ProdutoEntity selected) {
        this.selected = selected;
    }

    public List<ProdutoEntity> getProdutoList() {
        return produtoFacade.findAll();
    }

    // === CRUD ===
    public void adicionarProduto() {
        produtoFacade.create(produto);
        produto = new ProdutoEntity();
    }

    public void editarProduto() {
        if (selected != null) {
            produtoFacade.edit(selected);
            selected = null;
        }
    }

    public void deletarProduto() {
        if (selected != null) {
            produtoFacade.remove(selected);
            selected = null;
        }
    }
}
