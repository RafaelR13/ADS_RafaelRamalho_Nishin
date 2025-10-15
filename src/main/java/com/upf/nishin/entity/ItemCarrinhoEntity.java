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
@Table(name = "item_carrinho")
public class ItemCarrinhoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idItemCarrinho;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_carrinho", referencedColumnName = "idCarrinho")
    private CarrinhoEntity carrinho;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    private ProdutoEntity produto;

    private Integer quantidade;

    // ========= GETTERS E SETTERS =========

    public Integer getIdItemCarrinho() {
        return idItemCarrinho;
    }

    public void setIdItemCarrinho(Integer idItemCarrinho) {
        this.idItemCarrinho = idItemCarrinho;
    }

    public CarrinhoEntity getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(CarrinhoEntity carrinho) {
        this.carrinho = carrinho;
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

    public Double getSubtotal() {
        if (produto == null || produto.getPreco() == null) return 0.0;
        return produto.getPreco() * quantidade;
    }
}
