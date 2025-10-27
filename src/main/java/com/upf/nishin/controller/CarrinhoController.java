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
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("carrinhoController")
@SessionScoped
public class CarrinhoController implements Serializable {

    private CarrinhoEntity carrinho;
    private List<ItemCarrinhoEntity> itens = new ArrayList<>();

    @Inject
    private CarrinhoFacade carrinhoFacade;

    @Inject
    private ItemCarrinhoFacade itemCarrinhoFacade;

    @Inject
    private UsuarioFacade usuarioFacade;

    public CarrinhoEntity getCarrinho() {
        return carrinho;
    }

    public List<ItemCarrinhoEntity> getItens() {
        return itens;
    }

    /** Obtém o usuário logado da sessão **/
    private UsuarioEntity getUsuarioSessao() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        return session != null ? (UsuarioEntity) session.getAttribute("usuarioLogado") : null;
    }

    /** Garante que o carrinho do usuário exista no banco **/
    private void garantirCarrinhoUsuario() {
        UsuarioEntity usuario = getUsuarioSessao();

        if (usuario == null) {
            carrinho = null;
            itens = new ArrayList<>();
            return;
        }

        // Busca o carrinho existente
        carrinho = carrinhoFacade.findByUsuario(usuario);

        // Cria novo carrinho se não existir
        if (carrinho == null) {
            carrinho = new CarrinhoEntity();
            carrinho.setUsuario(usuario);
            carrinho.setDataAtualizacao(new Date());
            carrinhoFacade.create(carrinho);
        }

        // Carrega os itens do carrinho
        itens = itemCarrinhoFacade.findByCarrinho(carrinho);
    }

    /** Adiciona um produto ao carrinho (salvando no banco) **/
    public void adicionarProduto(ProdutoEntity produto) {
        FacesContext context = FacesContext.getCurrentInstance();
        UsuarioEntity usuario = getUsuarioSessao();

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Atenção", "Você precisa estar logado para adicionar produtos ao carrinho."));
            return;
        }

        garantirCarrinhoUsuario();

        // Verifica se o produto já existe no carrinho
        ItemCarrinhoEntity itemExistente = itemCarrinhoFacade.findByCarrinhoAndProduto(carrinho, produto);

        if (itemExistente != null) {
            itemExistente.setQuantidade(itemExistente.getQuantidade() + 1);
            itemCarrinhoFacade.edit(itemExistente);
        } else {
            ItemCarrinhoEntity novoItem = new ItemCarrinhoEntity();
            novoItem.setCarrinho(carrinho);
            novoItem.setProduto(produto);
            novoItem.setQuantidade(1);
            itemCarrinhoFacade.create(novoItem);
        }

        // Atualiza data do carrinho e lista
        carrinho.setDataAtualizacao(new Date());
        carrinhoFacade.edit(carrinho);
        itens = itemCarrinhoFacade.findByCarrinho(carrinho);

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Sucesso", produto.getNome() + " foi adicionado ao seu carrinho!"));
    }

    /** Redireciona para o carrinho ou login **/
    public String irParaCarrinho() {
        UsuarioEntity usuario = getUsuarioSessao();
        return (usuario == null)
                ? "/login.xhtml?faces-redirect=true"
                : "/carrinho.xhtml?faces-redirect=true";
    }

    /** Remove um item do carrinho **/
    public void removerItem(ItemCarrinhoEntity item) {
        if (item != null) {
            itemCarrinhoFacade.remove(item);
            itens.remove(item);
        }
    }

    /** Calcula o valor total **/
    public Double getValorTotal() {
        return itens.stream()
                .mapToDouble(i -> i.getProduto().getPreco() * i.getQuantidade())
                .sum();
    }

    /** Limpa todos os itens do carrinho **/
    public void limparCarrinho() {
        for (ItemCarrinhoEntity i : new ArrayList<>(itens)) {
            itemCarrinhoFacade.remove(i);
        }
        itens.clear();
    }

    /** Simula uma finalização de compra **/
    public void finalizarCompra() {
        limparCarrinho();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Compra finalizada!", "Seu pedido foi criado com sucesso!"));
    }

    /** Retorna quantidade total de produtos **/
    public int getTotalItens() {
        return itens != null
                ? itens.stream().mapToInt(ItemCarrinhoEntity::getQuantidade).sum()
                : 0;
    }
}
