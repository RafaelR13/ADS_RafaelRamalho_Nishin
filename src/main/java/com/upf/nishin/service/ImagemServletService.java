/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.service;

/**
 *
 * @author User
 */

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/imagem")
public class ImagemServletService extends HttpServlet {

    private static final String PASTA_UPLOAD = "C:/Nishin/uploads/";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String nomeArquivo = request.getParameter("nome");

        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File arquivo = new File(PASTA_UPLOAD + nomeArquivo);

        if (!arquivo.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String tipo = getServletContext().getMimeType(arquivo.getName());

        if (tipo == null) {
            tipo = "application/octet-stream";
        }

        response.setContentType(tipo);
        response.setContentLengthLong(arquivo.length());

        try (
                FileInputStream fis = new FileInputStream(arquivo);
                OutputStream os = response.getOutputStream()
        ) {

            byte[] buffer = new byte[4096];

            int bytesLidos;

            while ((bytesLidos = fis.read(buffer)) != -1) {

                os.write(buffer, 0, bytesLidos);

            }

        }

    }

}