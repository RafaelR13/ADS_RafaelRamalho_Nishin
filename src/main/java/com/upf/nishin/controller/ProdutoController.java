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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;

@Named("produtoController")
@SessionScoped
public class ProdutoController implements Serializable {

    private static final String PASTA_UPLOAD = "C:/Nishin/uploads/";

    private ProdutoEntity produto = new ProdutoEntity();
    private ProdutoEntity selected;

    private UploadedFile fileNovo;
    private UploadedFile fileEdit;

    @Inject
    private ProdutoFacade produtoFacade;

    /* ============================================================
       GETTERS / SETTERS
       ============================================================ */
    public ProdutoEntity getProduto() {
        return produto;
    }

    public void setProduto(ProdutoEntity produto) {
        this.produto = produto;
    }

    public ProdutoEntity getSelected() {
        return selected;
    }

    public void setSelected(ProdutoEntity selected) {
        this.selected = selected;
    }

    public UploadedFile getFileNovo() {
        return fileNovo;
    }

    public void setFileNovo(UploadedFile fileNovo) {
        this.fileNovo = fileNovo;
    }

    public UploadedFile getFileEdit() {
        return fileEdit;
    }

    public void setFileEdit(UploadedFile fileEdit) {
        this.fileEdit = fileEdit;
    }

    /* ============================================================
       LISTAGEM
       ============================================================ */
    public List<ProdutoEntity> getProdutoList() {
        return produtoFacade.findAll();
    }

    /* ============================================================
       UPLOAD
       ============================================================ */
    public void uploadNovoListener(FileUploadEvent event) {

        this.fileNovo = event.getFile();

        adicionarMensagem(
                FacesMessage.SEVERITY_INFO,
                "Imagem enviada",
                event.getFile().getFileName());
    }

    public void uploadEditListener(FileUploadEvent event) {

        this.fileEdit = event.getFile();

        adicionarMensagem(
                FacesMessage.SEVERITY_INFO,
                "Imagem enviada",
                event.getFile().getFileName());
    }

    private String salvarImagem(UploadedFile file) throws IOException {

        File pasta = new File(PASTA_UPLOAD);

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        String nomeArquivo
                = System.currentTimeMillis()
                + "_"
                + file.getFileName();

        Path destino = Paths.get(PASTA_UPLOAD, nomeArquivo);

        try (InputStream is = file.getInputStream()) {

            Files.copy(
                    is,
                    destino,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return "/imagem?nome=" + nomeArquivo;
    }

    private void excluirImagem(String caminhoImagem) {

        try {

            if (caminhoImagem == null || caminhoImagem.isBlank()) {
                return;
            }

            String nomeArquivo = caminhoImagem.replace("/imagem?nome=", "");

            File arquivo = new File(
                    "C:/Nishin/uploads/" + nomeArquivo);

            if (arquivo.exists()) {
                arquivo.delete();
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    /* ============================================================
       CRUD
       ============================================================ */
    public void adicionarProduto() {

        try {

            if (fileNovo != null
                    && !fileNovo.getFileName().isEmpty()) {

                String nomeImagem = salvarImagem(fileNovo);

                produto.setImagem(nomeImagem);
            }

            produtoFacade.create(produto);

            adicionarMensagem(
                    FacesMessage.SEVERITY_INFO,
                    "Sucesso",
                    "Produto cadastrado com sucesso!");

            produto = new ProdutoEntity();
            fileNovo = null;

        } catch (Exception e) {

            e.printStackTrace();

            adicionarMensagem(
                    FacesMessage.SEVERITY_ERROR,
                    "Erro",
                    "Não foi possível cadastrar o produto.");
        }
    }

    public void editarProduto() {

        try {

            if (selected == null) {
                return;
            }

            if (fileEdit != null
                    && !fileEdit.getFileName().isEmpty()) {

                // Apaga a imagem antiga
                excluirImagem(selected.getImagem());

                // Salva a nova imagem
                String nomeImagem = salvarImagem(fileEdit);

                // Atualiza o caminho no banco
                selected.setImagem(nomeImagem);
            }

            produtoFacade.edit(selected);

            adicionarMensagem(
                    FacesMessage.SEVERITY_INFO,
                    "Sucesso",
                    "Produto atualizado com sucesso!"
            );

            fileEdit = null;
            selected = null;

        } catch (Exception e) {

            e.printStackTrace();

            adicionarMensagem(
                    FacesMessage.SEVERITY_ERROR,
                    "Erro",
                    "Não foi possível atualizar o produto."
            );
        }
    }

    public void deletarProduto() {

        try {

            if (selected == null) {
                return;
            }

            excluirImagem(selected.getImagem());

            produtoFacade.remove(selected);

            adicionarMensagem(
                    FacesMessage.SEVERITY_INFO,
                    "Sucesso",
                    "Produto excluído com sucesso!"
            );

            selected = null;

        } catch (Exception e) {

            e.printStackTrace();

            adicionarMensagem(
                    FacesMessage.SEVERITY_ERROR,
                    "Erro",
                    "Falha ao excluir o produto."
            );
        }
    }

    /* ============================================================
       DETALHES
       ============================================================ */
    public String visualizarProduto(ProdutoEntity produtoSelecionado) {

        this.selected = produtoSelecionado;

        return "produtoDetalhe.xhtml?faces-redirect=true";
    }

    /**
     * Retorna o produto atualmente selecionado (para uso na página de
     * detalhes).
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
    private void adicionarMensagem(
            FacesMessage.Severity severity,
            String titulo,
            String detalhe) {

        FacesContext.getCurrentInstance()
                .addMessage(
                        null,
                        new FacesMessage(
                                severity,
                                titulo,
                                detalhe));
    }
}
