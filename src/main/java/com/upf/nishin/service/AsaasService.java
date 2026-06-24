/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.upf.nishin.service;

import com.upf.nishin.entity.EnderecoEntity;
import com.upf.nishin.entity.PedidoEntity;
import com.upf.nishin.entity.UsuarioEntity;
import com.upf.nishin.facade.EnderecoFacade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author User
 */

@ApplicationScoped
public class AsaasService {

    @Inject
    private EnderecoFacade enderecoFacade;

    private static final String API_URL
            = "http://localhost:8081/apiasaas/asaascobranca/criarcobranca";

    private static final String JWT_TOKEN
            = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyaWNhcmRvLnBpbmhlaXJvQGVtYWlsLmNvbSIsImlzcyI6InVwZiIsImlhdCI6MTc4MTgyMzgyNiwicGFzc3dvcmQiOiJzZGxranNkb2lqb25wdmY2NXY0ZTZmdjVlNnZlciIsImV4cCI6MTc4NDM0MzgyNn0.V1d4_ZQZtwScBDq5dz9VJAWybs_sJZD5qoZe1_OGKiA";

    private static final String EMPRESA_CNPJ
            = "22.682.160/0001-09";

    private static final String EMPRESA_API_KEY = carregarApiKey();

    private static String carregarApiKey() {

        try (InputStream input = AsaasService.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            Properties props = new Properties();
            props.load(input);

            return props.getProperty("asaas.api.key");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JsonObject criarCobranca(
            PedidoEntity pedido,
            UsuarioEntity usuario) {

        try {

            EnderecoEntity endereco
                    = enderecoFacade.buscarPorUsuario(usuario);

            if (endereco == null) {

                return Json.createObjectBuilder()
                        .add("error",
                                "Usuário não possui endereço cadastrado.")
                        .build();
            }

            JsonObject empresaDTO = Json.createObjectBuilder()
                    .add("empCnpj", EMPRESA_CNPJ)
                    .add("empAsaasapikey", EMPRESA_API_KEY)
                    .build();

            JsonObject clienteDTO = Json.createObjectBuilder()
                    .add("cpfCnpj", usuario.getCpf())
                    .add("name", usuario.getNome())
                    .add("email", usuario.getEmail())
                    .add("mobilePhone", usuario.getTelefone())
                    .add("postalCode", endereco.getCep())
                    .add("address", endereco.getLogradouro())
                    .add("addressNumber",
                            endereco.getNumeroEndereco())
                    .add("province",
                            endereco.getBairro())
                    .build();

            JsonObject payload = Json.createObjectBuilder()
                    .add("empresaDTO", empresaDTO)
                    .add("clienteDTO", clienteDTO)
                    .add("value", pedido.getValorTotal())
                    .add("billingType", "PIX")
                    .add("description",
                            "Pedido Nishin #" + pedido.getIdPedido())
                    .add("externalReference",
                            "PED-" + pedido.getIdPedido())
                    .add("dueDateLong",
                            System.currentTimeMillis())
                    .build();

            return enviar(payload);

        } catch (Exception e) {

            e.printStackTrace();

            return Json.createObjectBuilder()
                    .add("error", e.getMessage())
                    .build();
        }
    }

    private JsonObject enviar(JsonObject payload) {

        try {

            URL url = new URL(API_URL);

            HttpURLConnection con
                    = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");

            con.setDoOutput(true);

            con.setRequestProperty(
                    "Content-Type",
                    "application/json");

            con.setRequestProperty(
                    "Accept",
                    "application/json");

            con.setRequestProperty(
                    "token",
                    JWT_TOKEN);

            try (OutputStream os = con.getOutputStream()) {

                os.write(
                        payload.toString()
                                .getBytes("UTF-8"));
            }

            int status = con.getResponseCode();

            System.out.println("Status API: " + status);

            if (status >= 200 && status < 300) {

                try (JsonReader reader
                        = Json.createReader(
                                con.getInputStream())) {

                    return reader.readObject();
                }

            } else {

                try (JsonReader reader
                        = Json.createReader(
                                con.getErrorStream())) {

                    return reader.readObject();
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            return Json.createObjectBuilder()
                    .add("error", e.getMessage())
                    .build();
        }
    }
}
