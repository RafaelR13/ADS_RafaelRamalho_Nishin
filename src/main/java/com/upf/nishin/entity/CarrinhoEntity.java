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
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "carrinho")
public class CarrinhoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrinho;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private UsuarioEntity usuario;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAtualizacao = new Date();

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinhoEntity> itens;

    // ========= GETTERS E SETTERS =========

    public Integer getIdCarrinho() {
        return idCarrinho;
    }

    public void setIdCarrinho(Integer idCarrinho) {
        this.idCarrinho = idCarrinho;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public List<ItemCarrinhoEntity> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinhoEntity> itens) {
        this.itens = itens;
    }

    // ========= MÉTODOS DE APOIO =========

    public Double getValorTotal() {
        if (itens == null) return 0.0;
        return itens.stream()
                .mapToDouble(i -> i.getProduto().getPreco() * i.getQuantidade())
                .sum();
    }
}
