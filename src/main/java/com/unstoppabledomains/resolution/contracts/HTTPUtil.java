package com.unstoppabledomains.resolution.contracts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HTTPUtil {
    protected JsonObject post(String url, String address, String data) throws IOException {
        HttpURLConnection con = this.createAndConfigureCon(url);
        // Send 
        try (OutputStream os = con.getOutputStream()) {
            String body = this.prepareBody(data, address);
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        // Read the response
        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return (JsonObject) new JsonParser().parse(response.toString());
        }
    }

    protected HttpURLConnection createAndConfigureCon(String url) throws IOException {
        URL posturl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) posturl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        return con;
    }

    protected String prepareBody(String data, String address) {
        JsonArray params = this.prepareParamsForBody(data, address);
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("id", 1);
        body.addProperty("method", "eth_call");
        body.add("params", params);
        return body.toString();        
    }

    private JsonArray prepareParamsForBody(String data, String address) {
        JsonObject jo = new JsonObject();
        jo.addProperty("data", data);
        jo.addProperty("to", address);
        JsonArray params = new JsonArray();
        params.add(jo);
        params.add("latest");
        return params;
    }

}