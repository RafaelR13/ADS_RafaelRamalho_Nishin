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
import com.upf.nishin.entity.UsuarioEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class CarrinhoFacade extends AbstractFacade<CarrinhoEntity> {

    @PersistenceContext(unitName = "NishinPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CarrinhoFacade() {
        super(CarrinhoEntity.class);
    }

    /**
     * Busca o carrinho ativo associado a um usuário específico. Retorna null se
     * o usuário não tiver carrinho.
     */
    public CarrinhoEntity findByUsuario(UsuarioEntity usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            return null;
        }

        try {
            return getEntityManager()
                    .createQuery("SELECT c FROM CarrinhoEntity c WHERE c.usuario.idUsuario = :idUsuario", CarrinhoEntity.class)
                    .setParameter("idUsuario", usuario.getIdUsuario())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
