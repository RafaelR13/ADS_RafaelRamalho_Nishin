/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.facade;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.ProdutoEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ProdutoFacade extends AbstractFacade<ProdutoEntity> {

    @PersistenceContext(unitName = "NishinPU")
    private EntityManager em;

    public ProdutoFacade() {
        super(ProdutoEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}