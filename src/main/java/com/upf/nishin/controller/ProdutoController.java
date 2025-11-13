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
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

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
        try {
            produtoFacade.create(produto);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Produto cadastrado com sucesso!");
            produto = new ProdutoEntity(); // limpa o formulário
        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível adicionar o produto.");
        }
    }

    public void editarProduto() {
        if (selected != null) {
            try {
                produtoFacade.edit(selected);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Produto atualizado com sucesso!");
                selected = null;
            } catch (Exception e) {
                adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao atualizar o produto.");
            }
        }
    }

    public void deletarProduto() {
        if (selected != null) {
            try {
                produtoFacade.remove(selected);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Removido", "Produto excluído com sucesso!");
                selected = null;
            } catch (Exception e) {
                adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir o produto.");
            }
        }
    }

    /* ==================== DETALHES DO PRODUTO ==================== */
    /**
     * Define o produto clicado e redireciona para a página de detalhes.
     */
    public String visualizarProduto(ProdutoEntity produtoSelecionado) {
        this.selected = produtoSelecionado;
        return "produtoDetalhe.xhtml?faces-redirect=true";
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

    /* ==================== UTILITÁRIOS ==================== */
    private void adicionarMensagem(FacesMessage.Severity severity, String titulo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, titulo, detalhe));
    }
}
