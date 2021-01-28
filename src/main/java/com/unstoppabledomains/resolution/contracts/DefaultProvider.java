package com.unstoppabledomains.resolution.contracts;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unstoppabledomains.config.client.Client;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DefaultProvider implements IProvider {

  @Override
  public JsonObject request(String url, JsonObject body) throws IOException {
    HttpURLConnection con = createAndConfigureCon(url);
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
      return (JsonObject) JsonParser.parseString(response.toString());
    }
  }

  protected HttpURLConnection createAndConfigureCon(String url) throws IOException {
    URL posturl = new URL(url);
    HttpURLConnection con = (HttpURLConnection) posturl.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Accept", "application/json");
    con.addRequestProperty("User-Agent", getUserAgent());
    con.setDoOutput(true);
    return con;
  }

  protected String getUserAgent() {
    String agent = "UnstoppableDomains/resolution-java";
    String version = Client.getVersion();
    return version.isEmpty() ? agent : agent + "/" + version;
  }

}
