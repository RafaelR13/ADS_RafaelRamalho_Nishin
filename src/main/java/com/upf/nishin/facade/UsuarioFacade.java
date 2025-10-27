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
import jakarta.persistence.Query;

@Stateless
public class UsuarioFacade extends AbstractFacade<UsuarioEntity> {

    @PersistenceContext(unitName = "NishinPU")
    private EntityManager em;

    public UsuarioFacade() {
        super(UsuarioEntity.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Buscar um usuario por email
     * @param email
     * @param senha
     * @return 
     */
    public UsuarioEntity buscarPorEmail(String email, String senha) {
        UsuarioEntity usuario = new UsuarioEntity();
        try {
            //utilizando JPQL para construir a query 
            Query query = getEntityManager()
                    .createQuery("SELECT u FROM UsuarioEntity u WHERE u.email = :email AND u.senha = :senha");
            query.setParameter("email", email);
            query.setParameter("senha", senha);

            //verifica se existe algum resultado para não gerar excessão
            if (!query.getResultList().isEmpty()) {
                usuario = (UsuarioEntity) query.getSingleResult();
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e);
        }
        return usuario;
    }
}

