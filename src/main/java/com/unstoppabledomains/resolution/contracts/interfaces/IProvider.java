package com.unstoppabledomains.resolution.contracts.interfaces;

import java.io.IOException;

import com.google.gson.JsonObject;

public interface IProvider {
  /**
   * This method is making an http POST request with specific url and body
   * @param url - string representation of an endpoint
   * @param body - JsonObject containing everything that is needed
   * @return - returns JsonObject as a result from the url
   * @throws IOException
   */
  abstract JsonObject request(String url, JsonObject body) throws IOException;

  /**
   * This method allows to set extra headers to requests
   * @param key header key
   * @param value header value
   * @return IProvider
   */
  abstract IProvider setHeader(String key, String value);
}
