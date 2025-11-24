/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.controller;

/**
 *
 * @author User
 */

import com.upf.nishin.entity.ProdutoEntity;
import com.upf.nishin.facade.ProdutoFacade;
import com.upf.nishin.service.R2Service;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named("produtoController")
@SessionScoped
public class ProdutoController implements Serializable {

    private ProdutoEntity produto = new ProdutoEntity();
    private ProdutoEntity selected;

    private UploadedFile fileNovo;
    private UploadedFile fileEdit;

    @Inject
    private ProdutoFacade produtoFacade;

    @Inject
    private R2Service r2Service;

    /* ============================================================
       GETTERS / SETTERS
       ============================================================ */
    public ProdutoEntity getProduto() { return produto; }
    public void setProduto(ProdutoEntity produto) { this.produto = produto; }

    public ProdutoEntity getSelected() { return selected; }
    public void setSelected(ProdutoEntity selected) { this.selected = selected; }

    public UploadedFile getFileNovo() { return fileNovo; }
    public void setFileNovo(UploadedFile fileNovo) { this.fileNovo = fileNovo; }

    public UploadedFile getFileEdit() { return fileEdit; }
    public void setFileEdit(UploadedFile fileEdit) { this.fileEdit = fileEdit; }

    /* ============================================================
       LISTAGEM
       ============================================================ */
    public List<ProdutoEntity> getProdutoList() {
        return produtoFacade.findAll();
    }

    /* ============================================================
       LISTENERS DE UPLOAD
       ============================================================ */
    public void uploadNovoListener(FileUploadEvent event) {
        this.fileNovo = event.getFile();
        adicionarMensagem(FacesMessage.SEVERITY_INFO,
                "Imagem enviada", event.getFile().getFileName());
    }

    public void uploadEditListener(FileUploadEvent event) {
        this.fileEdit = event.getFile();
        adicionarMensagem(FacesMessage.SEVERITY_INFO,
                "Imagem enviada", event.getFile().getFileName());
    }

    /* ============================================================
       CRUD
       ============================================================ */
    public void adicionarProduto() {
        try {
            if (fileNovo != null && !fileNovo.getFileName().isEmpty()) {
                String url = uploadToR2(fileNovo);
                produto.setImagem(url);
            }

            produtoFacade.create(produto);

            adicionarMensagem(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Produto cadastrado com sucesso!");

            produto = new ProdutoEntity();
            fileNovo = null;

        } catch (Exception e) {
            e.printStackTrace();
            adicionarMensagem(FacesMessage.SEVERITY_ERROR,
                    "Erro", "Não foi possível cadastrar o produto.");
        }
    }

    public void editarProduto() {
        try {
            if (selected == null) return;

            if (fileEdit != null && !fileEdit.getFileName().isEmpty()) {
                String url = uploadToR2(fileEdit);
                selected.setImagem(url);
            }

            produtoFacade.edit(selected);

            adicionarMensagem(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Produto atualizado com sucesso!");

            fileEdit = null;
            selected = null;

        } catch (Exception e) {
            e.printStackTrace();
            adicionarMensagem(FacesMessage.SEVERITY_ERROR,
                    "Erro", "Não foi possível atualizar o produto.");
        }
    }

    public void deletarProduto() {
        try {
            if (selected == null) return;

            produtoFacade.remove(selected);

            adicionarMensagem(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Produto excluído com sucesso!");

            selected = null;

        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR,
                    "Erro", "Falha ao excluir o produto.");
        }
    }

    /* ============================================================
       UPLOAD PARA R2
       ============================================================ */
    private String uploadToR2(UploadedFile file) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getFileName();
        byte[] bytes;

        try (InputStream is = file.getInputStream()) {
            bytes = is.readAllBytes();
        }

        return r2Service.upload(fileName, bytes, file.getContentType());
    }

    /* ============================================================
       DETALHES
       ============================================================ */
    public String visualizarProduto(ProdutoEntity produtoSelecionado) {
        this.selected = produtoSelecionado;
        return "produtoDetalhe.xhtml?faces-redirect=true";
    }

    /**
     * Retorna o produto atualmente selecionado (para uso na página de detalhes).
     */
    public ProdutoEntity getProdutoSelecionado() {
        return selected;
    }

    /**
     * Limpa o produto selecionado, caso o usuário volte à página inicial.
     */
    public void limparSelecao() {
        this.selected = null;
    }

    /* ============================================================
       MENSAGENS
       ============================================================ */
    private void adicionarMensagem(FacesMessage.Severity severity, String titulo, String detalhe) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, titulo, detalhe));
    }
}
