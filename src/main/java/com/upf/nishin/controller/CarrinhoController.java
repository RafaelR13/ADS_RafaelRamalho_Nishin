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
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("carrinhoController")
@SessionScoped
public class CarrinhoController implements Serializable {

    private CarrinhoEntity carrinho = new CarrinhoEntity();

    @Inject
    private CarrinhoFacade carrinhoFacade;

    @Inject
    private ItemCarrinhoFacade itemCarrinhoFacade;

    private List<ItemCarrinhoEntity> itens = new ArrayList<>();

    public CarrinhoEntity getCarrinho() {
        return carrinho;
    }

    public List<ItemCarrinhoEntity> getItens() {
        return itens;
    }

    public void adicionarProduto(ProdutoEntity produto) {
        ItemCarrinhoEntity itemExistente = itens.stream()
                .filter(i -> i.getProduto().getId().equals(produto.getId()))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setQuantidade(itemExistente.getQuantidade() + 1);
        } else {
            ItemCarrinhoEntity novoItem = new ItemCarrinhoEntity();
            novoItem.setProduto(produto);
            novoItem.setQuantidade(1);
            itens.add(novoItem);
        }
    }

    public void removerItem(ItemCarrinhoEntity item) {
        itens.remove(item);
    }

    public Double getValorTotal() {
        return itens.stream()
                .mapToDouble(ItemCarrinhoEntity::getSubtotal)
                .sum();
    }

    public void limparCarrinho() {
        itens.clear();
    }

    public void finalizarCompra() {
        // Aqui você poderia salvar o carrinho no banco, gerar pedido, etc.
        carrinho.setItens(itens);
        carrinhoFacade.create(carrinho);
        limparCarrinho();
    }
}

