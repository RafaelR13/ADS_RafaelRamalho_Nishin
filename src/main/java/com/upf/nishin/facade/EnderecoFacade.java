/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.facade;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.EnderecoEntity;
import com.upf.nishin.entity.UsuarioEntity;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class EnderecoFacade {

    @PersistenceContext(unitName = "NishinPU")
    private EntityManager em;

    public void create(EnderecoEntity endereco) {
        em.persist(endereco);
    }

    public void edit(EnderecoEntity endereco) {
        em.merge(endereco);
    }

    public void remove(EnderecoEntity endereco) {
        em.remove(em.merge(endereco));
    }

    public EnderecoEntity find(Integer id) {
        return em.find(EnderecoEntity.class, id);
    }

    public EnderecoEntity buscarPorUsuario(UsuarioEntity usuario) {

        try {

            return em.createQuery(
                    "SELECT e FROM EnderecoEntity e "
                    + "WHERE e.usuario = :usuario",
                    EnderecoEntity.class)
                    .setParameter("usuario", usuario)
                    .getSingleResult();

        } catch (Exception e) {

            return null;
        }
    }
}
