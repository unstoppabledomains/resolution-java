package com.unstoppabledomains.resolution.contracts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unstoppabledomains.config.client.Client;

public class HTTPUtil {
    private HTTPUtil() {
    }

    public static JsonObject prepareBody(String method, JsonArray params) {
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("id", 1);
        body.addProperty("method", method);
        body.add("params", params);
        return body;
    }

    public static JsonObject post(String url, JsonObject body) throws IOException {
        HttpURLConnection con = HTTPUtil.createAndConfigureCon(url);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return (JsonObject) new JsonParser().parse(response.toString());
        }
    }

    private static HttpURLConnection createAndConfigureCon(String url) throws IOException {
        URL posturl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) posturl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.addRequestProperty("User-Agent", getUserAgent());
        con.setDoOutput(true);
        return con;
    }

    private static String getUserAgent() {
        String agent = "UnstoppableDomains/resolution-java";
        String version = Client.getVersion();
        return version.isEmpty() ? agent : agent + "/" + version;
    }
}