/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.facade;

/**
 *
 * @author User
 */
import com.upf.nishin.entity.CarrinhoEntity;
import com.upf.nishin.entity.ItemCarrinhoEntity;
import com.upf.nishin.entity.ProdutoEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

@Stateless
public class ItemCarrinhoFacade extends AbstractFacade<ItemCarrinhoEntity> {

    @PersistenceContext(unitName = "NishinPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemCarrinhoFacade() {
        super(ItemCarrinhoEntity.class);
    }

    /** Retorna todos os itens de um carrinho **/
    public List<ItemCarrinhoEntity> findByCarrinho(CarrinhoEntity carrinho) {
        if (carrinho == null) {
            return Collections.emptyList();
        }
        TypedQuery<ItemCarrinhoEntity> q = em.createQuery(
                "SELECT i FROM ItemCarrinhoEntity i WHERE i.carrinho = :carrinho",
                ItemCarrinhoEntity.class);
        q.setParameter("carrinho", carrinho);
        return q.getResultList();
    }

    /** Busca item específico de produto + carrinho **/
    public ItemCarrinhoEntity findByCarrinhoAndProduto(CarrinhoEntity carrinho, ProdutoEntity produto) {
    if (carrinho == null || produto == null) {
        return null;
    }

    TypedQuery<ItemCarrinhoEntity> q = em.createQuery(
        "SELECT i FROM ItemCarrinhoEntity i " +
        "WHERE i.carrinho.idCarrinho = :idCarrinho " +
        "AND i.produto.idProduto = :idProduto",
        ItemCarrinhoEntity.class
    );

    q.setParameter("idCarrinho", carrinho.getIdCarrinho());
    q.setParameter("idProduto", produto.getIdProduto());

    q.setMaxResults(1);
    return q.getResultStream().findFirst().orElse(null);
}

}
