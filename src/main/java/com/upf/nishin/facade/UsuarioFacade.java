/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.facade;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.UsuarioEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UsuarioFacade extends AbstractFacade<UsuarioEntity> {

    @PersistenceContext(unitName = "NishinPU")
    private EntityManager em;

    public UsuarioFacade() {
        super(UsuarioEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioEntity findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM UsuarioEntity u WHERE u.email = :email", UsuarioEntity.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}

