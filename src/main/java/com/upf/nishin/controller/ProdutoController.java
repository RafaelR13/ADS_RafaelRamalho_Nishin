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

/**
 * Controlador de produtos da Nishin Store.
 * Agora inclui suporte para exibir detalhes de um produto ao clicar no card.
 */
@Named("produtoController")
@SessionScoped
public class ProdutoController implements Serializable {

    private ProdutoEntity produto = new ProdutoEntity();
    private ProdutoEntity selected; // produto selecionado para edição ou detalhes

    @Inject
    private ProdutoFacade produtoFacade;

    /* ==================== GETTERS / SETTERS ==================== */
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

    /* ==================== LISTAGEM ==================== */
    public List<ProdutoEntity> getProdutoList() {
        return produtoFacade.findAll();
    }

    /* ==================== CRUD ==================== */
    public void adicionarProduto() {
        produtoFacade.create(produto);
        produto = new ProdutoEntity(); // limpa o formulário
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

    /* ==================== DETALHES DO PRODUTO ==================== */
    /**
     * Define o produto clicado e redireciona para a página de detalhes.
     * Pode ser chamado por um p:commandLink no card do produto.
     */
    public String visualizarProduto(ProdutoEntity produtoSelecionado) {
        this.selected = produtoSelecionado;
        return "detalhesProduto.xhtml?faces-redirect=true";
    }

    /**
     * Retorna o produto atualmente selecionado (para uso na página de detalhes).
     */
    public ProdutoEntity getProdutoSelecionado() {
        return selected;
    }

    /**
     * Limpa o produto selecionado, caso o usuário volte à página inicial.
     */
    public void limparSelecao() {
        this.selected = null;
    }
}

