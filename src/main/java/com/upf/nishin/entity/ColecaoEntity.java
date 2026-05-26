/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.entity;

/**
 *
 * @author User
 */

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "colecao")
public class ColecaoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colecao")
    private Integer idColecao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private UsuarioEntity usuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(length = 50)
    private String privacidade;

    @Column(name = "tema_config", columnDefinition = "TEXT")
    private String temaConfig;

    // ========= GETTERS E SETTERS =========

    public Integer getIdColecao() {
        return idColecao;
    }

    public void setIdColecao(Integer idColecao) {
        this.idColecao = idColecao;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrivacidade() {
        return privacidade;
    }

    public void setPrivacidade(String privacidade) {
        this.privacidade = privacidade;
    }

    public String getTemaConfig() {
        return temaConfig;
    }

    public void setTemaConfig(String temaConfig) {
        this.temaConfig = temaConfig;
    }
}