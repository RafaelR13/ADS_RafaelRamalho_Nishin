/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.facade;

/**
 *
 * @author User
 */
import com.upf.nishin.entity.EstoqueProdutoEntity;
import com.upf.nishin.entity.ProdutoEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Stateless
public class EstoqueProdutoFacade extends AbstractFacade<EstoqueProdutoEntity> {

    @PersistenceContext(unitName = "NishinPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstoqueProdutoFacade() {
        super(EstoqueProdutoEntity.class);
    }

    /**
     * Retorna a quantidade total disponível de um produto
     */
    public int buscarQuantidadeDisponivel(Integer idProduto) {

        if (idProduto == null) {
            return 0;
        }

        try {
            Long quantidade = getEntityManager()
                    .createQuery(
                            "SELECT COALESCE(SUM(e.quantidade),0) "
                            + "FROM EstoqueProdutoEntity e "
                            + "WHERE e.produto.idProduto = :idProduto",
                            Long.class)
                    .setParameter("idProduto", idProduto)
                    .getSingleResult();

            return quantidade != null ? quantidade.intValue() : 0;

        } catch (NoResultException e) {
            return 0;
        }
    }

    /**
     * Busca registro de estoque por produto
     */
    public EstoqueProdutoEntity findByProduto(ProdutoEntity produto) {

        if (produto == null || produto.getIdProduto() == null) {
            return null;
        }

        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT e FROM EstoqueProdutoEntity e "
                            + "WHERE e.produto.idProduto = :idProduto",
                            EstoqueProdutoEntity.class)
                    .setParameter("idProduto", produto.getIdProduto())
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }
}
