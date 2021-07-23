package com.unstoppabledomains.resolution.contracts;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;

public class JsonProvider extends DefaultProvider {
  private String method = "GET";
  private Gson gson;
  
  public JsonProvider() {
    super();
    gson = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .create();
  }
  
  public String getMethod() {
    return method;
  }
  
  public void setMethod(String method) {
    this.method = method;
  }

  public <T> T request(String url, java.lang.Class<T> classOfT) throws IOException {
    return this.request(url, null, classOfT);
  }

  public <T> T request(String url, JsonObject body, java.lang.Class<T> classOfT) throws IOException {
    String rawResponse = super.rawRequest(url, body);
    return gson.fromJson(rawResponse, classOfT);
  }
  
  @Override
  protected HttpURLConnection createAndConfigureCon(String url) throws IOException {
    HttpURLConnection conn = super.createAndConfigureCon(url);
    conn.setRequestMethod(method);
    return conn;
  }
}
