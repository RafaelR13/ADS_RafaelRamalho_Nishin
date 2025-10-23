/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.entity;

/**
 *
 * @author User
 */

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "item_pedido")
public class ItemPedidoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idItemPedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_pedido", referencedColumnName = "idPedido")
    private PedidoEntity pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    private ProdutoEntity produto;

    private Integer quantidade;
    private Double precoUnitario;

    // ========= GETTERS E SETTERS =========

    public Integer getIdItemPedido() {
        return idItemPedido;
    }

    public void setIdItemPedido(Integer idItemPedido) {
        this.idItemPedido = idItemPedido;
    }

    public PedidoEntity getPedido() {
        return pedido;
    }

    public void setPedido(PedidoEntity pedido) {
        this.pedido = pedido;
    }

    public ProdutoEntity getProduto() {
        return produto;
    }

    public void setProduto(ProdutoEntity produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Double getSubtotal() {
        if (quantidade == null || precoUnitario == null) return 0.0;
        return quantidade * precoUnitario;
    }
}