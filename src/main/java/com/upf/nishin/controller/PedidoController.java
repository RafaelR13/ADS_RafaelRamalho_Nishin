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

@Named("pedidoController")
@SessionScoped
public class PedidoController implements Serializable {

    @Inject
    private PedidoFacade pedidoFacade;

    @Inject
    private ItemPedidoFacade itemPedidoFacade;

    @Inject
    private CarrinhoController carrinhoController; // vamos aproveitar o carrinho atual

    private PedidoEntity pedidoAtual;

    private List<PedidoEntity> pedidosUsuario = new ArrayList<>();

    // ========= GETTERS =========
    public PedidoEntity getPedidoAtual() {
        return pedidoAtual;
    }

    public List<PedidoEntity> getPedidosUsuario() {
        return pedidosUsuario;
    }

    // ========= CRIAR PEDIDO =========
    public void criarPedido(UsuarioEntity usuario) {
        if (carrinhoController.getItens().isEmpty()) {
            return; // Carrinho vazio
        }

        pedidoAtual = new PedidoEntity();
        pedidoAtual.setUsuario(usuario);
        pedidoAtual.setStatus("PENDENTE");

        double total = 0.0;
        List<ItemPedidoEntity> itensPedido = new ArrayList<>();

        for (ItemCarrinhoEntity itemCarrinho : carrinhoController.getItens()) {
            ItemPedidoEntity itemPedido = new ItemPedidoEntity();
            itemPedido.setPedido(pedidoAtual);
            itemPedido.setProduto(itemCarrinho.getProduto());
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(itemCarrinho.getProduto().getPreco());

            total += itemPedido.getSubtotal();
            itensPedido.add(itemPedido);
        }

        pedidoAtual.setValorTotal(total);
        pedidoAtual.setItens(itensPedido);

        pedidoFacade.create(pedidoAtual);
        for (ItemPedidoEntity i : itensPedido) {
            itemPedidoFacade.create(i);
        }

        carrinhoController.limparCarrinho();
    }

    // ========= LISTAR PEDIDOS =========
    public List<PedidoEntity> listarPedidosUsuario(UsuarioEntity usuario) {
        return pedidoFacade.getEntityManager()
                .createQuery("SELECT p FROM PedidoEntity p WHERE p.usuario = :u ORDER BY p.dataPedido DESC", PedidoEntity.class)
                .setParameter("u", usuario)
                .getResultList();
    }
}
