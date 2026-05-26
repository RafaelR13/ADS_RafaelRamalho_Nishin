/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.controller;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.ColecaoEntity;
import com.upf.nishin.facade.ColecaoFacade;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ColecaoController implements Serializable {

    private ColecaoEntity colecao;

    private List<ColecaoEntity> colecoes;

    @Inject
    private ColecaoFacade colecaoFacade;

    @PostConstruct
    public void init() {
        colecao = new ColecaoEntity();
        carregarColecoes();
    }

    public void carregarColecoes() {
        try {
            colecoes = colecaoFacade.findAll();
        } catch (Exception e) {
            colecoes = new ArrayList<>();
            exibirErro("Erro ao carregar coleções.");
        }
    }

    public void salvarColecao() {
        try {

            if (colecao.getIdColecao() == null) {
                colecaoFacade.create(colecao);

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Sucesso",
                                "Coleção cadastrada com sucesso.")
                );

            } else {
                colecaoFacade.edit(colecao);

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Sucesso",
                                "Coleção atualizada com sucesso.")
                );
            }

            colecao = new ColecaoEntity();
            carregarColecoes();

        } catch (Exception e) {
            exibirErro("Erro ao salvar coleção.");
        }
    }

    public void editarColecao(ColecaoEntity colecaoSelecionada) {
        this.colecao = colecaoSelecionada;
    }

    public void excluirColecao(ColecaoEntity colecaoSelecionada) {
        try {

            colecaoFacade.remove(colecaoSelecionada);

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Sucesso",
                            "Coleção removida com sucesso.")
            );

            carregarColecoes();

        } catch (Exception e) {
            exibirErro("Erro ao excluir coleção.");
        }
    }

    public void aplicarTema(ColecaoEntity colecaoSelecionada) {
        try {

            // Exemplo:
            // Aqui você pode salvar o tema ativo na sessão
            // ou futuramente atualizar um campo no banco.

            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap()
                    .put("temaAtivo", colecaoSelecionada.getTemaConfig());

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Tema aplicado",
                            "Tema aplicado com sucesso.")
            );

        } catch (Exception e) {
            exibirErro("Erro ao aplicar tema.");
        }
    }

    private void exibirErro(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Erro",
                        mensagem
                )
        );
    }

    // ==========================
    // GETTERS E SETTERS
    // ==========================

    public ColecaoEntity getColecao() {
        return colecao;
    }

    public void setColecao(ColecaoEntity colecao) {
        this.colecao = colecao;
    }

    public List<ColecaoEntity> getColecoes() {
        return colecoes;
    }

    public void setColecoes(List<ColecaoEntity> colecoes) {
        this.colecoes = colecoes;
    }
}