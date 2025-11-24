/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author User
 */
@ApplicationScoped
public class AsaasService {

    private static final String API_URL = "http://localhost:8080/apiasaas/asaascobranca/criarcobranca";

    // 🔐 TOKEN JWT FIXO NO HEADER
    private static final String JWT_TOKEN
            = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyaWNhcmRvLnBpbmhlaXJvQGVtYWlsLmNvbSIsImlzcyI6InVwZiIsImlhdCI6MTc1NTc4MzI5NiwicGFzc3dvcmQiOiJzZGxranNkb2lqb25wdmY2NXY0ZTZmdjVlNnZlciIsImV4cCI6MTc1ODMwMzI5Nn0.j4pc3rr3YckMrDJasho6WSK5Xkjv8loJxtAXlndlmcc";

    public JsonObject criarCobranca(JsonObject payload) {

        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            // Headers
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            // 🔥 ENVIA O JWT AQUI
            con.setRequestProperty("Authorization", "Bearer " + JWT_TOKEN);

            // Envia o JSON
            try (OutputStream os = con.getOutputStream()) {
                os.write(payload.toString().getBytes("UTF-8"));
            }

            // Lê a resposta da API
            try (jakarta.json.JsonReader reader
                    = jakarta.json.Json.createReader(con.getInputStream())) {
                return reader.readObject();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return jakarta.json.Json.createObjectBuilder()
                    .add("error", e.getMessage())
                    .build();
        }
    }

}
