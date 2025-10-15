/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.facade;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.ItemCarrinhoEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
}

