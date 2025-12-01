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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.upf.nishin.dto.DadosPagamentoDTO;
import com.upf.nishin.facade.PedidoFacade;
import com.upf.nishin.facade.ItemPedidoFacade;

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

    @Inject
    private PedidoFacade pedidoFacade;

    @Inject
    private ItemPedidoFacade itemPedidoFacade;

    public CarrinhoEntity getCarrinho() {
        return carrinho;
    }

    public List<ItemCarrinhoEntity> getItens() {
        return itens;
    }

    private DadosPagamentoDTO dadosPagamento = new DadosPagamentoDTO();
    private boolean dialogPagamentoAberto = false;

    public DadosPagamentoDTO getDadosPagamento() {
        return dadosPagamento;
    }

    public boolean isDialogPagamentoAberto() {
        return dialogPagamentoAberto;
    }

    /**
     * Obtém o usuário logado da sessão *
     */
    private UsuarioEntity getUsuarioSessao() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        return session != null ? (UsuarioEntity) session.getAttribute("usuarioLogado") : null;
    }

    /**
     * Garante que o carrinho do usuário exista no banco *
     */
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

    /**
     * Adiciona um produto ao carrinho (salvando no banco) *
     */
    public String adicionarProduto(ProdutoEntity produto) {
        FacesContext context = FacesContext.getCurrentInstance();
        UsuarioEntity usuario = getUsuarioSessao();

        if (usuario == null) {
            return "/login.xhtml?faces-redirect=true";
        }

        garantirCarrinhoUsuario();

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

        carrinho.setDataAtualizacao(new Date());
        carrinhoFacade.edit(carrinho);
        itens = itemCarrinhoFacade.findByCarrinho(carrinho);

        // ✅ Usando Flash Scope para manter a mensagem após o redirect
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Sucesso", produto.getNome() + " foi adicionado ao seu carrinho!"));

        // ✅ Retorna para o index.xhtml com redirect
        return "/index.xhtml?faces-redirect=true";
    }

    public String verificarLoginAntesDeAdicionar(ProdutoEntity produto) {
        FacesContext context = FacesContext.getCurrentInstance();
        UsuarioEntity usuario = getUsuarioSessao();

        // 🔒 Se o usuário não estiver logado, manda pro login
        if (usuario == null) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    "Atenção",
                    "Você precisa estar logado para adicionar produtos ao carrinho."
            ));
            return "login.xhtml?faces-redirect=true";
        }

        try {
            // Adiciona o produto ao carrinho
            adicionarProduto(produto);

            // ✅ Mensagem de sucesso
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Sucesso",
                    produto.getNome() + " foi adicionado ao seu carrinho!"
            ));

            // 🔁 Redireciona para o index
            return "index.xhtml?faces-redirect=true";

        } catch (Exception e) {
            // ❌ Caso ocorra erro
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Erro",
                    "Ocorreu um problema ao adicionar o produto ao carrinho."
            ));
            return null;
        }
    }

    /**
     * Redireciona para o carrinho ou login *
     */
    public String irParaCarrinho() {
        UsuarioEntity usuario = getUsuarioSessao();
        return (usuario == null)
                ? "/login.xhtml?faces-redirect=true"
                : "/carrinho.xhtml?faces-redirect=true";
    }

    /**
     * Remove um item do carrinho *
     */
    public void removerItem(ItemCarrinhoEntity item) {
        if (item != null) {
            itemCarrinhoFacade.remove(item);
            itens.remove(item);
        }
    }

    /**
     * Calcula o valor total *
     */
    public Double getValorTotal() {
        return itens.stream()
                .mapToDouble(i -> i.getProduto().getPreco() * i.getQuantidade())
                .sum();
    }

    /**
     * Limpa todos os itens do carrinho *
     */
    public void limparCarrinho() {
        for (ItemCarrinhoEntity i : new ArrayList<>(itens)) {
            itemCarrinhoFacade.remove(i);
        }
        itens.clear();
    }

    public void abrirDialogPagamento() {

        UsuarioEntity usuario = getUsuarioSessao();
        if (usuario == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Você precisa estar logado!", null));
            return;
        }

        garantirCarrinhoUsuario();

        dadosPagamento = new DadosPagamentoDTO();
        dadosPagamento.setValorTotal(getValorTotal());

        dialogPagamentoAberto = true;
    }

    /**
     * Retorna quantidade total de produtos *
     */
    public int getTotalItens() {
        return itens != null
                ? itens.stream().mapToInt(ItemCarrinhoEntity::getQuantidade).sum()
                : 0;
    }

    public void confirmarPagamento() {
        try {
            UsuarioEntity usuario = getUsuarioSessao();

            if (usuario == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Erro", "Usuário não está logado."));
                return;
            }

            garantirCarrinhoUsuario();

            // 🔥 Criar o novo pedido
            PedidoEntity pedido = new PedidoEntity();
            pedido.setUsuario(usuario);
            pedido.setDataPedido(new Date());
            pedido.setStatus("PENDENTE");
            pedido.setValorTotal(getValorTotal());

            pedidoFacade.create(pedido);

            // 🔥 Salvar cada item do carrinho dentro do pedido
            for (ItemCarrinhoEntity item : itens) {

                ItemPedidoEntity ip = new ItemPedidoEntity();

                ip.setPedido(pedido);
                ip.setProduto(item.getProduto());
                ip.setQuantidade(item.getQuantidade());
                ip.setPrecoUnitario(item.getProduto().getPreco());

                itemPedidoFacade.create(ip);
            }

            // 🔥 Limpar o carrinho
            limparCarrinho();

            dialogPagamentoAberto = false;

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Compra finalizada!",
                            "Seu pedido foi registrado com sucesso."));

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erro ao processar pagamento.", null));
        }
    }

}
