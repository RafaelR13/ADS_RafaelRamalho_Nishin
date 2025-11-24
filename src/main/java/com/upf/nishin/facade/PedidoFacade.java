/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.facade;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.PedidoEntity;
import com.upf.nishin.entity.UsuarioEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PedidoFacade extends AbstractFacade<PedidoEntity> {

    @PersistenceContext(unitName = "NishinPU")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public PedidoFacade() {
        super(PedidoEntity.class);
    }

    public List<PedidoEntity> listarPedidosPorUsuario(UsuarioEntity usuario) {
        return em.createQuery(
                "SELECT p FROM PedidoEntity p WHERE p.usuario = :usuario ORDER BY p.dataPedido DESC",
                PedidoEntity.class
        )
        .setParameter("usuario", usuario)
        .getResultList();
    }
}
