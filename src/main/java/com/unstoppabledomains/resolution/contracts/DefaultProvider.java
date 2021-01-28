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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultProvider implements IProvider {

  private Map<String, String> headers;

  /**
   * Default constructor
   */
  public DefaultProvider() {
    headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");
    headers.put("Accept", "application/json");
    headers.put("User-Agent", getUserAgent());
  }

  /**
   * Constructor with empty headers
   * @return DefaultProvider for chaining
   */
  static public DefaultProvider cleanBuild() {
    DefaultProvider provider = new DefaultProvider();
    provider.headers = new HashMap<String, String>();
    return provider;
  }

  /**
   * Set the header for future builds
   * @param key header key
   * @param value header value
   * @return DefaultProvider
   */
  public DefaultProvider setHeader(String key, String value) {
    headers.put(key, value);
    return this;
  }

  public Map<String,String> getHeaders() {
    return headers;
  }

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
    for(Entry<String, String> entry:headers.entrySet()) {
      con.setRequestProperty(entry.getKey(), entry.getValue());
    }
    con.setDoOutput(true);
    return con;
  }

  protected String getUserAgent() {
    String agent = "UnstoppableDomains/resolution-java";
    String version = Client.getVersion();
    return version.isEmpty() ? agent : agent + "/" + version;
  }
}
