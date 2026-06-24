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
import com.upf.nishin.facade.EstoqueProdutoFacade;
import com.upf.nishin.service.AsaasService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

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

    @Inject
    private EstoqueProdutoFacade estoqueProdutoFacade;

    @Inject
    private AsaasService asaasService;

    public CarrinhoEntity getCarrinho() {
        return carrinho;
    }

    public List<ItemCarrinhoEntity> getItens() {
        return itens;
    }

    private int quantidadeSelecionada = 1;
    private DadosPagamentoDTO dadosPagamento = new DadosPagamentoDTO();
    private boolean dialogPagamentoAberto = false;

    public DadosPagamentoDTO getDadosPagamento() {
        return dadosPagamento;
    }

    public boolean isDialogPagamentoAberto() {
        return dialogPagamentoAberto;
    }

    public int getQuantidadeSelecionada() {
        return quantidadeSelecionada;
    }

    public void setQuantidadeSelecionada(int quantidadeSelecionada) {
        this.quantidadeSelecionada = quantidadeSelecionada;
    }

    public int getEstoqueDisponivel(ProdutoEntity produto) {
        return estoqueProdutoFacade.buscarQuantidadeDisponivel(produto.getIdProduto());
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
    public String adicionarProduto(ProdutoEntity produto, int quantidade) {
        FacesContext context = FacesContext.getCurrentInstance();
        UsuarioEntity usuario = getUsuarioSessao();

        if (usuario == null) {
            return "/login.xhtml?faces-redirect=true";
        }

        garantirCarrinhoUsuario();

        int estoqueDisponivel = getEstoqueDisponivel(produto);

        if (quantidade <= 0) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    "Quantidade inválida",
                    "Selecione ao menos 1 unidade."
            ));
            return null;
        }

        if (quantidade > estoqueDisponivel) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Estoque insuficiente",
                    "Quantidade máxima disponível: " + estoqueDisponivel
            ));
            return null;
        }

        ItemCarrinhoEntity itemExistente = itemCarrinhoFacade.findByCarrinhoAndProduto(carrinho, produto);

        int quantidadeFinal = quantidade;

        if (itemExistente != null) {
            quantidadeFinal += itemExistente.getQuantidade();
        }

        if (quantidadeFinal > estoqueDisponivel) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Limite excedido",
                    "Você já possui itens no carrinho. Máximo permitido: " + estoqueDisponivel
            ));
            return null;
        }

        if (itemExistente != null) {
            itemExistente.setQuantidade(quantidadeFinal);
            itemCarrinhoFacade.edit(itemExistente);
        } else {
            ItemCarrinhoEntity novoItem = new ItemCarrinhoEntity();
            novoItem.setCarrinho(carrinho);
            novoItem.setProduto(produto);
            novoItem.setQuantidade(quantidade);
            itemCarrinhoFacade.create(novoItem);
        }

        carrinho.setDataAtualizacao(new Date());
        carrinhoFacade.edit(carrinho);
        itens = itemCarrinhoFacade.findByCarrinho(carrinho);

        // ✅ Usando Flash Scope para manter a mensagem após o redirect
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Sucesso",
                produto.getNome() + " foi adicionado ao carrinho!"
        ));

        quantidadeSelecionada = 1;

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
            // Adiciona o produto ao carrinho com a quantidade selecionada
            adicionarProduto(produto, quantidadeSelecionada);

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

    public void atualizarQuantidade(ItemCarrinhoEntity item) {

        if (item == null || item.getProduto() == null) {
            return;
        }

        int estoqueDisponivel = getEstoqueDisponivel(item.getProduto());

        // Quantidade mínima
        if (item.getQuantidade() < 1) {
            item.setQuantidade(1);
        }

        // Limite do estoque
        if (item.getQuantidade() > estoqueDisponivel) {

            item.setQuantidade(estoqueDisponivel);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Estoque insuficiente",
                            "Máximo disponível: " + estoqueDisponivel
                    ));
        }

        // Salva no banco
        itemCarrinhoFacade.edit(item);

        // Atualiza lista
        itens = itemCarrinhoFacade.findByCarrinho(carrinho);
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

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Erro",
                                "Usuário não está logado."
                        ));

                return;
            }

            garantirCarrinhoUsuario();

            // Cria pedido
            PedidoEntity pedido = new PedidoEntity();

            pedido.setUsuario(usuario);
            pedido.setDataPedido(new Date());
            pedido.setStatus("PENDENTE");
            pedido.setValorTotal(getValorTotal());

            pedidoFacade.create(pedido);

            // Cria cobrança na API
            JsonObject resposta = asaasService.criarCobranca(pedido, usuario);

            // Verifica erro
            if (resposta.containsKey("error")) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Erro ao criar cobrança",
                                resposta.getString("error")
                        ));

                return;
            }

            // Salva itens do pedido
            for (ItemCarrinhoEntity item : itens) {

                ItemPedidoEntity ip = new ItemPedidoEntity();

                ip.setPedido(pedido);
                ip.setProduto(item.getProduto());
                ip.setQuantidade(item.getQuantidade());
                ip.setPrecoUnitario(item.getProduto().getPreco());

                itemPedidoFacade.create(ip);
            }

            pedido.setStatus("AGUARDANDO_PAGAMENTO");

            pedidoFacade.edit(pedido);

            limparCarrinho();

            dialogPagamentoAberto = false;

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Pedido criado",
                            "Cobrança PIX criada com sucesso."
                    ));

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro",
                            "Não foi possível finalizar a compra."
                    ));
        }
    }
}